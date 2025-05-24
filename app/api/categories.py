from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, Category, Subcategory, CategoryType

router = APIRouter(prefix="/api/categories", tags=["categories"])


# Schema models
class CategoryBase(BaseModel):
    name: str
    type: CategoryType


class CategoryCreate(CategoryBase):
    pass


class CategoryUpdate(CategoryBase):
    name: Optional[str] = None
    type: Optional[CategoryType] = None


class CategoryResponse(CategoryBase):
    id: int

    class Config:
        orm_mode = True


class CategoryWithSubcategories(CategoryResponse):
    subcategories: List["SubcategoryResponse"] = []


# CRUD operations
@router.post("/", response_model=CategoryResponse, status_code=status.HTTP_201_CREATED)
async def create_category(
    category: CategoryCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Category:
    """Create a new category."""
    db_category = Category(**category.dict())
    db.add(db_category)
    await db.commit()
    await db.refresh(db_category)
    return db_category


@router.get("/", response_model=List[CategoryResponse])
async def read_categories(
    skip: int = 0,
    limit: int = 100,
    type_filter: Optional[CategoryType] = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[Category]:
    """Get all categories with optional filtering by type."""
    query = select(Category)
    
    if type_filter:
        query = query.filter(Category.type == type_filter)
    
    query = query.offset(skip).limit(limit)
    result = await db.execute(query)
    return result.scalars().all()


@router.get("/{category_id}", response_model=CategoryResponse)
async def read_category(
    category_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Category:
    """Get a specific category by ID."""
    result = await db.execute(select(Category).filter(Category.id == category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    return db_category


@router.put("/{category_id}", response_model=CategoryResponse)
async def update_category(
    category_id: int,
    category: CategoryUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Category:
    """Update a category."""
    result = await db.execute(select(Category).filter(Category.id == category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    update_data = category.dict(exclude_unset=True)
    for key, value in update_data.items():
        setattr(db_category, key, value)
    
    await db.commit()
    await db.refresh(db_category)
    return db_category


@router.delete("/{category_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_category(
    category_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> None:
    """Delete a category."""
    result = await db.execute(select(Category).filter(Category.id == category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    await db.delete(db_category)
    await db.commit()


@router.get("/{category_id}/subcategories", response_model=List["SubcategoryResponse"])
async def read_category_subcategories(
    category_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[Subcategory]:
    """Get all subcategories for a specific category."""
    result = await db.execute(select(Category).filter(Category.id == category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    result = await db.execute(
        select(Subcategory).filter(Subcategory.category_id == category_id)
    )
    return result.scalars().all()


# Import at the end to avoid circular imports
from app.api.subcategories import SubcategoryResponse