import os
from pathlib import Path
from typing import Optional, Dict, Any, List

from pydantic import BaseModel
from dotenv import load_dotenv

# Load environment variables from .env file if it exists
load_dotenv()

# Base directory of the application
BASE_DIR = Path(__file__).resolve().parent.parent

# Database settings
DATABASE_URL = os.getenv("DATABASE_URL", f"sqlite:///{BASE_DIR}/data/budget.db")

# JWT settings
SECRET_KEY = os.getenv("SECRET_KEY", "your_secret_key_here_change_in_production")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24  # 24 hours

# Available currencies
AVAILABLE_CURRENCIES = ["ZAR", "INR"]
DEFAULT_CURRENCY = "ZAR"

# CORS settings - restrict to local network by default
CORS_ORIGINS = [
    "http://localhost",
    "http://localhost:8000",
    "http://127.0.0.1",
    "http://127.0.0.1:8000",
]

# Application settings
APP_NAME = "Budget Tracker"
APP_VERSION = "1.0.0"
APP_DESCRIPTION = "A lightweight budget tracking application with multi-currency support"


class Settings(BaseModel):
    app_name: str = APP_NAME
    app_version: str = APP_VERSION
    app_description: str = APP_DESCRIPTION
    database_url: str = DATABASE_URL
    secret_key: str = SECRET_KEY
    algorithm: str = ALGORITHM
    access_token_expire_minutes: int = ACCESS_TOKEN_EXPIRE_MINUTES
    available_currencies: List[str] = AVAILABLE_CURRENCIES
    default_currency: str = DEFAULT_CURRENCY
    cors_origins: List[str] = CORS_ORIGINS


# Create settings instance
settings = Settings()