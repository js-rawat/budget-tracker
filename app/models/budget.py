from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Enum
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
import enum
from datetime import datetime

from app.database import Base


class PeriodType(str, enum.Enum):
    MONTHLY = "monthly"
    YEARLY = "yearly"


class Budget(Base):
    __tablename__ = "budgets"

    id = Column(Integer, primary_key=True, index=True)
    category_id = Column(Integer, ForeignKey("categories.id"))
    subcategory_id = Column(Integer, ForeignKey("subcategories.id"), nullable=True)
    amount = Column(Float)
    currency = Column(String, default="ZAR")
    start_date = Column(DateTime)
    end_date = Column(DateTime)
    period_type = Column(Enum(PeriodType), default=PeriodType.MONTHLY)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    category = relationship("Category", back_populates="budgets")
    subcategory = relationship("Subcategory", back_populates="budgets")

    @property
    def is_active(self):
        """Check if the budget is currently active."""
        now = datetime.now()
        return self.start_date <= now <= self.end_date