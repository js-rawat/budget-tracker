from typing import List, Optional
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import func, and_, extract
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, Category, Subcategory, Transaction, CategoryType
from app.config import settings

router = APIRouter(prefix="/api/transactions", tags=["transactions"])


# Schema models
class TransactionBase(BaseModel):
    category_id: int
    subcategory_id: Optional[int] = None
    amount: float
    currency: str = settings.default_currency
    transaction_date: datetime
    description: str
    type: CategoryType


class TransactionCreate(TransactionBase):
    pass


class TransactionUpdate(BaseModel):
    category_id: Optional[int] = None
    subcategory_id: Optional[int] = None
    amount: Optional[float] = None
    currency: Optional[str] = None
    transaction_date: Optional[datetime] = None
    description: Optional[str] = None
    type: Optional[CategoryType] = None


class TransactionResponse(TransactionBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        orm_mode = True


class TransactionSummaryItem(BaseModel):
    category_id: int
    category_name: str
    subcategory_id: Optional[int] = None
    subcategory_name: Optional[str] = None
    total_amount: float
    transaction_count: int
    currency: str
    type: CategoryType


class TransactionSummary(BaseModel):
    items: List[TransactionSummaryItem]
    total_income: float
    total_expense: float
    net_amount: float
    currency: str
    period: str


# CRUD operations
@router.post("/", response_model=TransactionResponse, status_code=status.HTTP_201_CREATED)
async def create_transaction(
    transaction: TransactionCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Transaction:
    """Create a new transaction."""
    # Verify that the category exists and matches the transaction type
    result = await db.execute(select(Category).filter(Category.id == transaction.category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    if db_category.type != transaction.type:
        raise HTTPException(
            status_code=400,
            detail=f"Category type ({db_category.type}) does not match transaction type ({transaction.type})"
        )
    
    # Verify that the subcategory exists if provided
    if transaction.subcategory_id:
        result = await db.execute(
            select(Subcategory).filter(
                Subcategory.id == transaction.subcategory_id,
                Subcategory.category_id == transaction.category_id
            )
        )
        db_subcategory = result.scalars().first()
        
        if db_subcategory is None:
            raise HTTPException(
                status_code=404, 
                detail="Subcategory not found or does not belong to the specified category"
            )
    
    # Verify that the currency is valid
    if transaction.currency not in settings.available_currencies:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid currency. Available currencies: {', '.join(settings.available_currencies)}"
        )
    
    db_transaction = Transaction(**transaction.dict())
    db.add(db_transaction)
    await db.commit()
    await db.refresh(db_transaction)
    return db_transaction


@router.get("/", response_model=List[TransactionResponse])
async def read_transactions(
    skip: int = 0,
    limit: int = 100,
    category_id: Optional[int] = None,
    subcategory_id: Optional[int] = None,
    transaction_type: Optional[CategoryType] = None,
    currency: Optional[str] = None,
    start_date: Optional[datetime] = None,
    end_date: Optional[datetime] = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[Transaction]:
    """Get all transactions with optional filtering."""
    query = select(Transaction)
    
    # Apply filters
    if category_id:
        query = query.filter(Transaction.category_id == category_id)
    
    if subcategory_id:
        query = query.filter(Transaction.subcategory_id == subcategory_id)
    
    if transaction_type:
        query = query.filter(Transaction.type == transaction_type)
    
    if currency:
        query = query.filter(Transaction.currency == currency)
    
    if start_date:
        query = query.filter(Transaction.transaction_date >= start_date)
    
    if end_date:
        query = query.filter(Transaction.transaction_date <= end_date)
    
    # Order by transaction date (newest first)
    query = query.order_by(Transaction.transaction_date.desc())
    
    query = query.offset(skip).limit(limit)
    result = await db.execute(query)
    return result.scalars().all()


@router.get("/{transaction_id}", response_model=TransactionResponse)
async def read_transaction(
    transaction_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Transaction:
    """Get a specific transaction by ID."""
    result = await db.execute(select(Transaction).filter(Transaction.id == transaction_id))
    db_transaction = result.scalars().first()
    
    if db_transaction is None:
        raise HTTPException(status_code=404, detail="Transaction not found")
    
    return db_transaction


@router.put("/{transaction_id}", response_model=TransactionResponse)
async def update_transaction(
    transaction_id: int,
    transaction: TransactionUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Transaction:
    """Update a transaction."""
    result = await db.execute(select(Transaction).filter(Transaction.id == transaction_id))
    db_transaction = result.scalars().first()
    
    if db_transaction is None:
        raise HTTPException(status_code=404, detail="Transaction not found")
    
    update_data = transaction.dict(exclude_unset=True)
    
    # If category_id or type is being updated, verify that they match
    category_id = update_data.get("category_id", db_transaction.category_id)
    transaction_type = update_data.get("type", db_transaction.type)
    
    if "category_id" in update_data or "type" in update_data:
        result = await db.execute(select(Category).filter(Category.id == category_id))
        db_category = result.scalars().first()
        
        if db_category is None:
            raise HTTPException(status_code=404, detail="Category not found")
        
        if db_category.type != transaction_type:
            raise HTTPException(
                status_code=400,
                detail=f"Category type ({db_category.type}) does not match transaction type ({transaction_type})"
            )
    
    # Verify that the subcategory exists and belongs to the category if being updated
    if "subcategory_id" in update_data and update_data["subcategory_id"] is not None:
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
    
    for key, value in update_data.items():
        setattr(db_transaction, key, value)
    
    await db.commit()
    await db.refresh(db_transaction)
    return db_transaction


@router.delete("/{transaction_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_transaction(
    transaction_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> None:
    """Delete a transaction."""
    result = await db.execute(select(Transaction).filter(Transaction.id == transaction_id))
    db_transaction = result.scalars().first()
    
    if db_transaction is None:
        raise HTTPException(status_code=404, detail="Transaction not found")
    
    await db.delete(db_transaction)
    await db.commit()


@router.get("/summary", response_model=TransactionSummary)
async def get_transaction_summary(
    start_date: datetime = Query(..., description="Start date for the summary period"),
    end_date: datetime = Query(..., description="End date for the summary period"),
    currency: str = Query(settings.default_currency, description="Currency for the summary"),
    group_by: str = Query("category", description="Group by 'category' or 'subcategory'"),
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> TransactionSummary:
    """Get a summary of transactions for a period."""
    # Validate group_by parameter
    if group_by not in ["category", "subcategory"]:
        raise HTTPException(
            status_code=400,
            detail="group_by must be 'category' or 'subcategory'"
        )
    
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
    total_income = 0
    total_expense = 0
    
    # Group transactions by category or subcategory
    transaction_dict = {}
    count_dict = {}
    
    for transaction in transactions:
        key = (transaction.category_id, transaction.subcategory_id if group_by == "subcategory" else None)
        
        if key not in transaction_dict:
            transaction_dict[key] = 0
            count_dict[key] = 0
        
        transaction_dict[key] += transaction.amount
        count_dict[key] += 1
        
        if transaction.type == CategoryType.INCOME:
            total_income += transaction.amount
        else:
            total_expense += transaction.amount
    
    # Create summary items
    for (cat_id, subcat_id), total_amount in transaction_dict.items():
        # Get category and subcategory names
        result = await db.execute(select(Category).filter(Category.id == cat_id))
        category = result.scalars().first()
        
        subcategory = None
        if subcat_id:
            result = await db.execute(select(Subcategory).filter(Subcategory.id == subcat_id))
            subcategory = result.scalars().first()
        
        summary_items.append(
            TransactionSummaryItem(
                category_id=cat_id,
                category_name=category.name if category else "Unknown",
                subcategory_id=subcat_id,
                subcategory_name=subcategory.name if subcategory else None,
                total_amount=total_amount,
                transaction_count=count_dict[(cat_id, subcat_id)],
                currency=currency,
                type=category.type if category else CategoryType.EXPENSE
            )
        )
    
    # Calculate net amount
    net_amount = total_income - total_expense
    
    return TransactionSummary(
        items=summary_items,
        total_income=total_income,
        total_expense=total_expense,
        net_amount=net_amount,
        currency=currency,
        period=f"{start_date.strftime('%Y-%m-%d')} to {end_date.strftime('%Y-%m-%d')}"
    )