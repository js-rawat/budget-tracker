from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from pydantic import BaseModel

from app.auth.utils import get_current_active_user
from app.database import get_db
from app.models import User, Category, Subcategory

router = APIRouter(prefix="/api/subcategories", tags=["subcategories"])


# Schema models
class SubcategoryBase(BaseModel):
    name: str
    category_id: int


class SubcategoryCreate(SubcategoryBase):
    pass


class SubcategoryUpdate(BaseModel):
    name: Optional[str] = None
    category_id: Optional[int] = None


class SubcategoryResponse(SubcategoryBase):
    id: int

    class Config:
        orm_mode = True


# CRUD operations
@router.post("/", response_model=SubcategoryResponse, status_code=status.HTTP_201_CREATED)
async def create_subcategory(
    subcategory: SubcategoryCreate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Subcategory:
    """Create a new subcategory."""
    # Verify that the category exists
    result = await db.execute(select(Category).filter(Category.id == subcategory.category_id))
    db_category = result.scalars().first()
    
    if db_category is None:
        raise HTTPException(status_code=404, detail="Category not found")
    
    db_subcategory = Subcategory(**subcategory.dict())
    db.add(db_subcategory)
    await db.commit()
    await db.refresh(db_subcategory)
    return db_subcategory


@router.get("/", response_model=List[SubcategoryResponse])
async def read_subcategories(
    skip: int = 0,
    limit: int = 100,
    category_id: Optional[int] = None,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> List[Subcategory]:
    """Get all subcategories with optional filtering by category."""
    query = select(Subcategory)
    
    if category_id:
        query = query.filter(Subcategory.category_id == category_id)
    
    query = query.offset(skip).limit(limit)
    result = await db.execute(query)
    return result.scalars().all()


@router.get("/{subcategory_id}", response_model=SubcategoryResponse)
async def read_subcategory(
    subcategory_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Subcategory:
    """Get a specific subcategory by ID."""
    result = await db.execute(select(Subcategory).filter(Subcategory.id == subcategory_id))
    db_subcategory = result.scalars().first()
    
    if db_subcategory is None:
        raise HTTPException(status_code=404, detail="Subcategory not found")
    
    return db_subcategory


@router.put("/{subcategory_id}", response_model=SubcategoryResponse)
async def update_subcategory(
    subcategory_id: int,
    subcategory: SubcategoryUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> Subcategory:
    """Update a subcategory."""
    result = await db.execute(select(Subcategory).filter(Subcategory.id == subcategory_id))
    db_subcategory = result.scalars().first()
    
    if db_subcategory is None:
        raise HTTPException(status_code=404, detail="Subcategory not found")
    
    update_data = subcategory.dict(exclude_unset=True)
    
    # If category_id is being updated, verify that the category exists
    if "category_id" in update_data:
        result = await db.execute(select(Category).filter(Category.id == update_data["category_id"]))
        db_category = result.scalars().first()
        
        if db_category is None:
            raise HTTPException(status_code=404, detail="Category not found")
    
    for key, value in update_data.items():
        setattr(db_subcategory, key, value)
    
    await db.commit()
    await db.refresh(db_subcategory)
    return db_subcategory


@router.delete("/{subcategory_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_subcategory(
    subcategory_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
) -> None:
    """Delete a subcategory."""
    result = await db.execute(select(Subcategory).filter(Subcategory.id == subcategory_id))
    db_subcategory = result.scalars().first()
    
    if db_subcategory is None:
        raise HTTPException(status_code=404, detail="Subcategory not found")
    
    await db.delete(db_subcategory)
    await db.commit()