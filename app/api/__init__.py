from app.api.categories import router as categories_router
from app.api.subcategories import router as subcategories_router
from app.api.budgets import router as budgets_router
from app.api.transactions import router as transactions_router
from app.api.reports import router as reports_router
from app.api.settings import router as settings_router

# List of all API routers
routers = [
    categories_router,
    subcategories_router,
    budgets_router,
    transactions_router,
    reports_router,
    settings_router
]