FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .

# Install problematic packages separately with specific versions first
RUN pip install --no-cache-dir --trusted-host pypi.org --trusted-host files.pythonhosted.org uvicorn==0.23.2
RUN pip install --no-cache-dir --trusted-host pypi.org --trusted-host files.pythonhosted.org pydantic==2.0.3

# Then install the rest of the requirements
RUN pip install --no-cache-dir --trusted-host pypi.org --trusted-host files.pythonhosted.org -r requirements.txt

# Copy application code
COPY . .

# Create data directory for SQLite database
RUN mkdir -p /app/data

# Set environment variables
ENV DATABASE_URL=sqlite:///data/budget.db
ENV SECRET_KEY=your_secret_key_here_change_in_production

# Expose the port
EXPOSE 8000

# Run the application
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]