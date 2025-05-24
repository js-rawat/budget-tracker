import pytest
from fastapi.testclient import TestClient
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker

from app.main import app
from app.database import Base, get_db
from app.models import User

# Create test database
TEST_DATABASE_URL = "sqlite+aiosqlite:///:memory:"
engine = create_async_engine(TEST_DATABASE_URL)
TestingSessionLocal = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)


# Dependency override
async def override_get_db():
    async with TestingSessionLocal() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close()


app.dependency_overrides[get_db] = override_get_db
client = TestClient(app)


@pytest.fixture
async def setup_database():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)


@pytest.mark.asyncio
async def test_register_user(setup_database):
    response = client.post(
        "/api/auth/register",
        json={"username": "testuser", "password": "testpassword", "default_currency": "ZAR"}
    )
    assert response.status_code == 200
    data = response.json()
    assert data["username"] == "testuser"
    assert data["default_currency"] == "ZAR"
    assert "id" in data


@pytest.mark.asyncio
async def test_login_user(setup_database):
    # First register a user
    client.post(
        "/api/auth/register",
        json={"username": "testuser", "password": "testpassword", "default_currency": "ZAR"}
    )
    
    # Then try to login
    response = client.post(
        "/api/auth/login",
        data={"username": "testuser", "password": "testpassword"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"


@pytest.mark.asyncio
async def test_get_current_user(setup_database):
    # First register a user
    client.post(
        "/api/auth/register",
        json={"username": "testuser", "password": "testpassword", "default_currency": "ZAR"}
    )
    
    # Then login to get token
    login_response = client.post(
        "/api/auth/login",
        data={"username": "testuser", "password": "testpassword"}
    )
    token = login_response.json()["access_token"]
    
    # Use token to get current user
    response = client.get(
        "/api/auth/me",
        headers={"Authorization": f"Bearer {token}"}
    )
    assert response.status_code == 200
    data = response.json()
    assert data["username"] == "testuser"
    assert data["default_currency"] == "ZAR"