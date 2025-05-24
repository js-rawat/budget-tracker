from sqlalchemy import Column, Integer, String, Float, DateTime, UniqueConstraint
from sqlalchemy.sql import func

from app.database import Base


class CurrencyRate(Base):
    __tablename__ = "currency_rates"

    id = Column(Integer, primary_key=True, index=True)
    from_currency = Column(String)
    to_currency = Column(String)
    rate = Column(Float)
    effective_date = Column(DateTime)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Ensure unique combination of from_currency, to_currency, and effective_date
    __table_args__ = (
        UniqueConstraint('from_currency', 'to_currency', 'effective_date', name='uix_currency_rate'),
    )