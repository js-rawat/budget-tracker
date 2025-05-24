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

## License

This project is licensed under the MIT License - see the LICENSE file for details.
