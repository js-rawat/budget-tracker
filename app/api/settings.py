from typing import List, Dict, Optional
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import and_
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, CurrencyRate
from app.config import settings

router = APIRouter(prefix="/api/settings", tags=["settings"])


class CurrencyRateBase(BaseModel):
    from_currency: str
    to_currency: str
    rate: float
    effective_date: datetime


class CurrencyRateCreate(CurrencyRateBase):
    pass


class CurrencyRateResponse(CurrencyRateBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        orm_mode = True


class AvailableCurrencies(BaseModel):
    currencies: List[str]
    default_currency: str


@router.get("/currencies", response_model=AvailableCurrencies)
async def get_available_currencies(
    current_user: User = Depends(get_current_active_user)
) -> AvailableCurrencies:
    """Get available currencies."""
    return AvailableCurrencies(
        currencies=settings.available_currencies,
        default_currency=settings.default_currency
    )


@router.get("/currencies/rates", response_model=List[CurrencyRateResponse])
async def get_currency_rates(
    from_currency: Optional[str] = None,
    to_currency: Optional[str] = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[CurrencyRate]:
    """Get currency exchange rates."""
    query = select(CurrencyRate)
    
    if from_currency:
        query = query.filter(CurrencyRate.from_currency == from_currency)
    
    if to_currency:
        query = query.filter(CurrencyRate.to_currency == to_currency)
    
    # Order by effective date (newest first)
    query = query.order_by(CurrencyRate.effective_date.desc())
    
    result = await db.execute(query)
    return result.scalars().all()


@router.post("/currencies/rates", response_model=CurrencyRateResponse, status_code=status.HTTP_201_CREATED)
async def create_currency_rate(
    currency_rate: CurrencyRateCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> CurrencyRate:
    """Create a new currency exchange rate."""
    # Verify that the currencies are valid
    if currency_rate.from_currency not in settings.available_currencies:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid from_currency. Available currencies: {', '.join(settings.available_currencies)}"
        )
    
    if currency_rate.to_currency not in settings.available_currencies:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid to_currency. Available currencies: {', '.join(settings.available_currencies)}"
        )
    
    if currency_rate.from_currency == currency_rate.to_currency:
        raise HTTPException(
            status_code=400,
            detail="from_currency and to_currency cannot be the same"
        )
    
    # Check if a rate already exists for the same currencies and effective date
    result = await db.execute(
        select(CurrencyRate).filter(
            and_(
                CurrencyRate.from_currency == currency_rate.from_currency,
                CurrencyRate.to_currency == currency_rate.to_currency,
                CurrencyRate.effective_date == currency_rate.effective_date
            )
        )
    )
    existing_rate = result.scalars().first()
    
    if existing_rate:
        # Update the existing rate
        existing_rate.rate = currency_rate.rate
        await db.commit()
        await db.refresh(existing_rate)
        return existing_rate
    
    # Create a new rate
    db_currency_rate = CurrencyRate(**currency_rate.dict())
    db.add(db_currency_rate)
    await db.commit()
    await db.refresh(db_currency_rate)
    return db_currency_rate


@router.get("/user/preferences", response_model=Dict[str, str])
async def get_user_preferences(
    current_user: User = Depends(get_current_active_user)
) -> Dict[str, str]:
    """Get user preferences."""
    return {
        "default_currency": current_user.default_currency
    }


@router.put("/user/preferences", response_model=Dict[str, str])
async def update_user_preferences(
    preferences: Dict[str, str],
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Dict[str, str]:
    """Update user preferences."""
    if "default_currency" in preferences:
        if preferences["default_currency"] not in settings.available_currencies:
            raise HTTPException(
                status_code=400, 
                detail=f"Invalid currency. Available currencies: {', '.join(settings.available_currencies)}"
            )
        
        current_user.default_currency = preferences["default_currency"]
        await db.commit()
    
    return {
        "default_currency": current_user.default_currency
    }