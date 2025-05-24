# Budget Tracker Application

A lightweight budget tracking application with SQLite persistence, designed to run on Raspberry Pi.

## Features

- Multi-currency support (ZAR and INR)
- Categories and subcategories for income and expenses
- Budget management with monthly/yearly periods
- Transaction tracking
- Visualization of budget trends with line graphs
- Monthly filtering for reports
- Responsive web interface accessible from any device on the local network

## Technology Stack

- **Backend**: Python with FastAPI
- **Frontend**: HTML, CSS, JavaScript with Alpine.js and Chart.js
- **Database**: SQLite
- **Authentication**: JWT (JSON Web Tokens)
- **Containerization**: Docker

## Requirements

- Docker and Docker Compose
- Raspberry Pi (or any system that can run Docker)

## Installation and Setup

### Using Docker Compose (Recommended)

1. Clone this repository:
   ```
   git clone <repository-url>
   cd budget-app
   ```

2. Build and start the application:
   ```
   docker-compose up -d
   ```

3. Access the application in your browser:
   ```
   http://localhost:8000
   ```

   Or if accessing from another device on the network:
   ```
   http://<raspberry-pi-ip>:8000
   ```

### Manual Setup (Development)

1. Clone this repository:
   ```
   git clone <repository-url>
   cd budget-app
   ```

2. Create a virtual environment and install dependencies:
   ```
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install -r requirements.txt
   ```

3. Run the application:
   ```
   uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
   ```

4. Access the application in your browser:
   ```
   http://localhost:8000
   ```

## Initial Setup

1. Register a new user account when you first access the application.
2. Create categories and subcategories for your income and expenses.
3. Set up budgets for your categories.
4. Start adding transactions.

## Data Persistence

The SQLite database is stored in the `data` directory. When using Docker, this directory is mounted as a volume to ensure data persistence between container restarts.

## Security Considerations

- The default configuration is intended for local network use only.
- Change the `SECRET_KEY` in the `.env` file or Docker environment variables for production use.
- Restrict access to the application by configuring your network properly.

## Troubleshooting

### Docker Build Issues

If you encounter 403 Forbidden errors when building the Docker image, it may be due to temporary issues with PyPI or network restrictions. Try these alternatives:

1. **Install specific package versions manually:**
   ```
   # Install problematic packages separately first
   pip install uvicorn==0.23.2
   pip install pydantic==2.0.3
   
   # Then install the rest of the requirements
   pip install -r requirements.txt
   ```

2. **Use the manual setup method described above** instead of Docker until the package repository issues are resolved.

3. **Try building with network adjustments:**
   ```
   # Add --network=host to your docker build command
   docker build --network=host -t budget-app .
   ```

### Running Without Docker

If Docker continues to be problematic, you can run the application directly:

1. Create and activate a virtual environment:
   ```
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

2. Install specific versions of problematic packages first:
   ```
   pip install uvicorn==0.23.2
   pip install pydantic==2.0.3
   ```

3. Install the remaining dependencies:
   ```
   pip install -r requirements.txt
   ```

4. Create the data directory:
   ```
   mkdir -p app/data
   ```

5. Use the provided .env file or set environment variables manually:
   ```
   # Option 1: Use the provided .env file (recommended)
   # The application will automatically load variables from the .env file
   
   # Option 2: Set environment variables manually
   # On Linux/macOS
   export DATABASE_URL=sqlite:///app/data/budget.db
   export SECRET_KEY=your_secret_key_here_change_in_production
   
   # On Windows
   set DATABASE_URL=sqlite:///app/data/budget.db
   set SECRET_KEY=your_secret_key_here_change_in_production
   ```

6. Run the application:
   ```
   uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
   ```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
