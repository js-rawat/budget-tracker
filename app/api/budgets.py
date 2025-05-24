from typing import List, Optional
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import func, and_
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, Category, Subcategory, Budget, PeriodType, Transaction
from app.config import settings

router = APIRouter(prefix="/api/budgets", tags=["budgets"])


# Schema models
class BudgetBase(BaseModel):
    category_id: int
    subcategory_id: Optional[int] = None
    amount: float
    currency: str = settings.default_currency
    start_date: datetime
    end_date: datetime
    period_type: PeriodType = PeriodType.MONTHLY


class BudgetCreate(BudgetBase):
    pass


class BudgetUpdate(BaseModel):
    category_id: Optional[int] = None
    subcategory_id: Optional[int] = None
    amount: Optional[float] = None
    currency: Optional[str] = None
    start_date: Optional[datetime] = None
    end_date: Optional[datetime] = None
    period_type: Optional[PeriodType] = None


class BudgetResponse(BudgetBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        orm_mode = True


class BudgetSummaryItem(BaseModel):
    category_id: int
    category_name: str
    subcategory_id: Optional[int] = None
    subcategory_name: Optional[str] = None
    budget_amount: float
    actual_amount: float
    currency: str
    percentage_used: float


class BudgetSummary(BaseModel):
    items: List[BudgetSummaryItem]
    total_budget: float
    total_actual: float
    overall_percentage: float
    currency: str
    period: str


# CRUD operations
@router.post("/", response_model=BudgetResponse, status_code=status.HTTP_201_CREATED)
async def create_budget(
    budget: BudgetCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Budget:
    """Create a new budget."""
    # Verify that the category exists
    result = await db.execute(select(Category).filter(Category.id == budget.category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    # Verify that the subcategory exists if provided
    if budget.subcategory_id:
        result = await db.execute(
            select(Subcategory).filter(
                Subcategory.id == budget.subcategory_id,
                Subcategory.category_id == budget.category_id
            )
        )
        db_subcategory = result.scalars().first()
        
        if db_subcategory is None:
            raise HTTPException(
                status_code=404, 
                detail="Subcategory not found or does not belong to the specified category"
            )
    
    # Verify that the currency is valid
    if budget.currency not in settings.available_currencies:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid currency. Available currencies: {', '.join(settings.available_currencies)}"
        )
    
    # Verify that start_date is before end_date
    if budget.start_date >= budget.end_date:
        raise HTTPException(
            status_code=400,
            detail="Start date must be before end date"
        )
    
    db_budget = Budget(**budget.dict())
    db.add(db_budget)
    await db.commit()
    await db.refresh(db_budget)
    return db_budget


@router.get("/", response_model=List[BudgetResponse])
async def read_budgets(
    skip: int = 0,
    limit: int = 100,
    category_id: Optional[int] = None,
    subcategory_id: Optional[int] = None,
    currency: Optional[str] = None,
    active_only: bool = False,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[Budget]:
    """Get all budgets with optional filtering."""
    query = select(Budget)
    
    # Apply filters
    if category_id:
        query = query.filter(Budget.category_id == category_id)
    
    if subcategory_id:
        query = query.filter(Budget.subcategory_id == subcategory_id)
    
    if currency:
        query = query.filter(Budget.currency == currency)
    
    if active_only:
        now = datetime.now()
        query = query.filter(
            and_(
                Budget.start_date <= now,
                Budget.end_date >= now
            )
        )
    
    query = query.offset(skip).limit(limit)
    result = await db.execute(query)
    return result.scalars().all()


@router.get("/{budget_id}", response_model=BudgetResponse)
async def read_budget(
    budget_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Budget:
    """Get a specific budget by ID."""
    result = await db.execute(select(Budget).filter(Budget.id == budget_id))
    db_budget = result.scalars().first()
    
    if db_budget is None:
        raise HTTPException(status_code=404, detail="Budget not found")
    
    return db_budget


@router.put("/{budget_id}", response_model=BudgetResponse)
async def update_budget(
    budget_id: int,
    budget: BudgetUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Budget:
    """Update a budget."""
    result = await db.execute(select(Budget).filter(Budget.id == budget_id))
    db_budget = result.scalars().first()
    
    if db_budget is None:
        raise HTTPException(status_code=404, detail="Budget not found")
    
    update_data = budget.dict(exclude_unset=True)
    
    # Verify that the category exists if being updated
    if "category_id" in update_data:
        result = await db.execute(select(Category).filter(Category.id == update_data["category_id"]))
        db_category = result.scalars().first()
        
        if db_category is None:
            raise HTTPException(status_code=404, detail="Category not found")
    
    # Verify that the subcategory exists and belongs to the category if being updated
    if "subcategory_id" in update_data and update_data["subcategory_id"] is not None:
        category_id = update_data.get("category_id", db_budget.category_id)
        result = await db.execute(
            select(Subcategory).filter(
                Subcategory.id == update_data["subcategory_id"],
                Subcategory.category_id == category_id
            )
        )
        db_subcategory = result.scalars().first()
        
        if db_subcategory is None:
            raise HTTPException(
                status_code=404, 
                detail="Subcategory not found or does not belong to the specified category"
            )
    
    # Verify that the currency is valid if being updated
    if "currency" in update_data and update_data["currency"] not in settings.available_currencies:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid currency. Available currencies: {', '.join(settings.available_currencies)}"
        )
    
    # Verify that start_date is before end_date if either is being updated
    start_date = update_data.get("start_date", db_budget.start_date)
    end_date = update_data.get("end_date", db_budget.end_date)
    
    if start_date >= end_date:
        raise HTTPException(
            status_code=400,
            detail="Start date must be before end date"
        )
    
    for key, value in update_data.items():
        setattr(db_budget, key, value)
    
    await db.commit()
    await db.refresh(db_budget)
    return db_budget


@router.delete("/{budget_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_budget(
    budget_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> None:
    """Delete a budget."""
    result = await db.execute(select(Budget).filter(Budget.id == budget_id))
    db_budget = result.scalars().first()
    
    if db_budget is None:
        raise HTTPException(status_code=404, detail="Budget not found")
    
    await db.delete(db_budget)
    await db.commit()


@router.get("/summary", response_model=BudgetSummary)
async def get_budget_summary(
    start_date: datetime = Query(..., description="Start date for the summary period"),
    end_date: datetime = Query(..., description="End date for the summary period"),
    currency: str = Query(settings.default_currency, description="Currency for the summary"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> BudgetSummary:
    """Get a summary of budgets vs. actual spending for a period."""
    # Get all budgets for the period
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
    
    # Get all transactions for the period
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
    
    # Calculate summary
    summary_items = []
    total_budget = 0
    total_actual = 0
    
    # Group budgets by category and subcategory
    budget_dict = {}
    for budget in budgets:
        key = (budget.category_id, budget.subcategory_id)
        if key not in budget_dict:
            budget_dict[key] = 0
        budget_dict[key] += budget.amount
        total_budget += budget.amount
    
    # Group transactions by category and subcategory
    transaction_dict = {}
    for transaction in transactions:
        key = (transaction.category_id, transaction.subcategory_id)
        if key not in transaction_dict:
            transaction_dict[key] = 0
        transaction_dict[key] += transaction.amount
        total_actual += transaction.amount
    
    # Create summary items
    for (cat_id, subcat_id), budget_amount in budget_dict.items():
        # Get category and subcategory names
        result = await db.execute(select(Category).filter(Category.id == cat_id))
        category = result.scalars().first()
        
        subcategory = None
        if subcat_id:
            result = await db.execute(select(Subcategory).filter(Subcategory.id == subcat_id))
            subcategory = result.scalars().first()
        
        # Get actual amount
        actual_amount = transaction_dict.get((cat_id, subcat_id), 0)
        
        # Calculate percentage
        percentage_used = (actual_amount / budget_amount * 100) if budget_amount > 0 else 0
        
        summary_items.append(
            BudgetSummaryItem(
                category_id=cat_id,
                category_name=category.name if category else "Unknown",
                subcategory_id=subcat_id,
                subcategory_name=subcategory.name if subcategory else None,
                budget_amount=budget_amount,
                actual_amount=actual_amount,
                currency=currency,
                percentage_used=percentage_used
            )
        )
    
    # Calculate overall percentage
    overall_percentage = (total_actual / total_budget * 100) if total_budget > 0 else 0
    
    return BudgetSummary(
        items=summary_items,
        total_budget=total_budget,
        total_actual=total_actual,
        overall_percentage=overall_percentage,
        currency=currency,
        period=f"{start_date.strftime('%Y-%m-%d')} to {end_date.strftime('%Y-%m-%d')}"
    )