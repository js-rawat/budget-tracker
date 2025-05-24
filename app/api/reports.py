from typing import List, Dict, Any, Optional
from datetime import datetime, timedelta
import calendar

from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import func, and_, extract
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, Category, Subcategory, Transaction, Budget, CategoryType
from app.config import settings

router = APIRouter(prefix="/api/reports", tags=["reports"])


class DataPoint(BaseModel):
    label: str
    value: float


class TrendData(BaseModel):
    labels: List[str]
    datasets: List[Dict[str, Any]]


class MonthlyReportData(BaseModel):
    income_by_category: List[DataPoint]
    expense_by_category: List[DataPoint]
    budget_vs_actual: TrendData
    daily_transactions: TrendData
    net_income_expense: TrendData


@router.get("/trends", response_model=TrendData)
async def get_budget_trends(
    start_date: datetime = Query(..., description="Start date for the trend data"),
    end_date: datetime = Query(..., description="End date for the trend data"),
    category_id: Optional[int] = Query(None, description="Filter by category ID"),
    subcategory_id: Optional[int] = Query(None, description="Filter by subcategory ID"),
    currency: str = Query(settings.default_currency, description="Currency for the trend data"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> TrendData:
    """Get budget trend data for visualization."""
    # Calculate the number of months between start_date and end_date
    months_diff = (end_date.year - start_date.year) * 12 + end_date.month - start_date.month + 1
    
    # Generate month labels
    labels = []
    current_date = start_date.replace(day=1)
    for _ in range(months_diff):
        labels.append(current_date.strftime("%b %Y"))
        # Move to next month
        if current_date.month == 12:
            current_date = current_date.replace(year=current_date.year + 1, month=1)
        else:
            current_date = current_date.replace(month=current_date.month + 1)
    
    # Initialize datasets
    budget_data = [0] * months_diff
    actual_data = [0] * months_diff
    
    # Get budget data
    budget_query = select(Budget).filter(
        and_(
            Budget.currency == currency,
            Budget.start_date <= end_date,
            Budget.end_date >= start_date
        )
    )
    
    if category_id:
        budget_query = budget_query.filter(Budget.category_id == category_id)
    
    if subcategory_id:
        budget_query = budget_query.filter(Budget.subcategory_id == subcategory_id)
    
    result = await db.execute(budget_query)
    budgets = result.scalars().all()
    
    # Process budget data
    for budget in budgets:
        # Calculate monthly budget amount
        if budget.period_type == "yearly":
            monthly_amount = budget.amount / 12
        else:
            monthly_amount = budget.amount
        
        # Determine which months this budget applies to
        budget_start = max(budget.start_date, start_date)
        budget_end = min(budget.end_date, end_date)
        
        current_month = budget_start.replace(day=1)
        while current_month <= budget_end:
            month_index = (current_month.year - start_date.year) * 12 + current_month.month - start_date.month
            if 0 <= month_index < months_diff:
                budget_data[month_index] += monthly_amount
            
            # Move to next month
            if current_month.month == 12:
                current_month = current_month.replace(year=current_month.year + 1, month=1)
            else:
                current_month = current_month.replace(month=current_month.month + 1)
    
    # Get transaction data
    transaction_query = select(Transaction).filter(
        and_(
            Transaction.currency == currency,
            Transaction.transaction_date >= start_date,
            Transaction.transaction_date <= end_date,
            Transaction.type == CategoryType.EXPENSE  # Only consider expenses for budget comparison
        )
    )
    
    if category_id:
        transaction_query = transaction_query.filter(Transaction.category_id == category_id)
    
    if subcategory_id:
        transaction_query = transaction_query.filter(Transaction.subcategory_id == subcategory_id)
    
    result = await db.execute(transaction_query)
    transactions = result.scalars().all()
    
    # Process transaction data
    for transaction in transactions:
        month_index = (transaction.transaction_date.year - start_date.year) * 12 + transaction.transaction_date.month - start_date.month
        if 0 <= month_index < months_diff:
            actual_data[month_index] += transaction.amount
    
    # Create datasets
    datasets = [
        {
            "label": "Budget",
            "data": budget_data,
            "backgroundColor": "rgba(54, 162, 235, 0.2)",
            "borderColor": "rgba(54, 162, 235, 1)",
            "borderWidth": 1
        },
        {
            "label": "Actual",
            "data": actual_data,
            "backgroundColor": "rgba(255, 99, 132, 0.2)",
            "borderColor": "rgba(255, 99, 132, 1)",
            "borderWidth": 1
        }
    ]
    
    return TrendData(labels=labels, datasets=datasets)


@router.get("/monthly/{year}/{month}", response_model=MonthlyReportData)
async def get_monthly_report(
    year: int,
    month: int,
    currency: str = Query(settings.default_currency, description="Currency for the report"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> MonthlyReportData:
    """Get monthly report data for visualization."""
    # Validate month
    if month < 1 or month > 12:
        raise HTTPException(status_code=400, detail="Month must be between 1 and 12")
    
    # Calculate start and end dates for the month
    start_date = datetime(year, month, 1)
    last_day = calendar.monthrange(year, month)[1]
    end_date = datetime(year, month, last_day, 23, 59, 59)
    
    # Get all transactions for the month
    result = await db.execute(
        select(Transaction).filter(
            and_(
                Transaction.currency == currency,
                Transaction.transaction_date >= start_date,
                Transaction.transaction_date <= end_date
            )
        )
    )
    transactions = result.scalars().all()
    
    # Get all budgets for the month
    result = await db.execute(
        select(Budget).filter(
            and_(
                Budget.currency == currency,
                Budget.start_date <= end_date,
                Budget.end_date >= start_date
            )
        )
    )
    budgets = result.scalars().all()
    
    # Process income by category
    income_by_category = {}
    for transaction in transactions:
        if transaction.type == CategoryType.INCOME:
            result = await db.execute(select(Category).filter(Category.id == transaction.category_id))
            category = result.scalars().first()
            category_name = category.name if category else "Unknown"
            
            if category_name not in income_by_category:
                income_by_category[category_name] = 0
            
            income_by_category[category_name] += transaction.amount
    
    income_data = [
        DataPoint(label=category, value=amount)
        for category, amount in income_by_category.items()
    ]
    
    # Process expense by category
    expense_by_category = {}
    for transaction in transactions:
        if transaction.type == CategoryType.EXPENSE:
            result = await db.execute(select(Category).filter(Category.id == transaction.category_id))
            category = result.scalars().first()
            category_name = category.name if category else "Unknown"
            
            if category_name not in expense_by_category:
                expense_by_category[category_name] = 0
            
            expense_by_category[category_name] += transaction.amount
    
    expense_data = [
        DataPoint(label=category, value=amount)
        for category, amount in expense_by_category.items()
    ]
    
    # Process budget vs actual
    budget_by_category = {}
    for budget in budgets:
        result = await db.execute(select(Category).filter(Category.id == budget.category_id))
        category = result.scalars().first()
        
        if category and category.type == CategoryType.EXPENSE:
            category_name = category.name
            
            if category_name not in budget_by_category:
                budget_by_category[category_name] = 0
            
            # Calculate monthly budget amount
            if budget.period_type == "yearly":
                monthly_amount = budget.amount / 12
            else:
                monthly_amount = budget.amount
            
            budget_by_category[category_name] += monthly_amount
    
    # Create budget vs actual datasets
    budget_vs_actual_labels = list(set(list(budget_by_category.keys()) + list(expense_by_category.keys())))
    budget_vs_actual_budget_data = [budget_by_category.get(label, 0) for label in budget_vs_actual_labels]
    budget_vs_actual_actual_data = [expense_by_category.get(label, 0) for label in budget_vs_actual_labels]
    
    budget_vs_actual = TrendData(
        labels=budget_vs_actual_labels,
        datasets=[
            {
                "label": "Budget",
                "data": budget_vs_actual_budget_data,
                "backgroundColor": "rgba(54, 162, 235, 0.2)",
                "borderColor": "rgba(54, 162, 235, 1)",
                "borderWidth": 1
            },
            {
                "label": "Actual",
                "data": budget_vs_actual_actual_data,
                "backgroundColor": "rgba(255, 99, 132, 0.2)",
                "borderColor": "rgba(255, 99, 132, 1)",
                "borderWidth": 1
            }
        ]
    )
    
    # Process daily transactions
    daily_income = [0] * last_day
    daily_expense = [0] * last_day
    
    for transaction in transactions:
        day_index = transaction.transaction_date.day - 1
        if transaction.type == CategoryType.INCOME:
            daily_income[day_index] += transaction.amount
        else:
            daily_expense[day_index] += transaction.amount
    
    daily_labels = [str(i) for i in range(1, last_day + 1)]
    daily_transactions = TrendData(
        labels=daily_labels,
        datasets=[
            {
                "label": "Income",
                "data": daily_income,
                "backgroundColor": "rgba(75, 192, 192, 0.2)",
                "borderColor": "rgba(75, 192, 192, 1)",
                "borderWidth": 1
            },
            {
                "label": "Expense",
                "data": daily_expense,
                "backgroundColor": "rgba(255, 99, 132, 0.2)",
                "borderColor": "rgba(255, 99, 132, 1)",
                "borderWidth": 1
            }
        ]
    )
    
    # Process net income/expense
    total_income = sum(daily_income)
    total_expense = sum(daily_expense)
    
    net_income_expense = TrendData(
        labels=["Income", "Expense", "Net"],
        datasets=[
            {
                "label": "Amount",
                "data": [total_income, total_expense, total_income - total_expense],
                "backgroundColor": [
                    "rgba(75, 192, 192, 0.2)",
                    "rgba(255, 99, 132, 0.2)",
                    "rgba(54, 162, 235, 0.2)"
                ],
                "borderColor": [
                    "rgba(75, 192, 192, 1)",
                    "rgba(255, 99, 132, 1)",
                    "rgba(54, 162, 235, 1)"
                ],
                "borderWidth": 1
            }
        ]
    )
    
    return MonthlyReportData(
        income_by_category=income_data,
        expense_by_category=expense_data,
        budget_vs_actual=budget_vs_actual,
        daily_transactions=daily_transactions,
        net_income_expense=net_income_expense
    )