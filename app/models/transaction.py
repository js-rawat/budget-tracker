from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Enum
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
import enum

from app.database import Base
from app.models.category import CategoryType


class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    category_id = Column(Integer, ForeignKey("categories.id"))
    subcategory_id = Column(Integer, ForeignKey("subcategories.id"), nullable=True)
    amount = Column(Float)
    currency = Column(String, default="ZAR")
    transaction_date = Column(DateTime)
    description = Column(String)
    type = Column(Enum(CategoryType))
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    category = relationship("Category", back_populates="transactions")
    subcategory = relationship("Subcategory", back_populates="transactions")