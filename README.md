# Budget Tracker Application

A comprehensive budget tracking application that supports multiple currencies (ZAR and INR), category-based expense tracking, and visual trend analysis.

## Features

- **Multi-Currency Support**: Track your finances in ZAR (South African Rand) and INR (Indian Rupee)
- **Category Management**: Organize expenses with categories and subcategories
- **Budget Planning**: Set monthly budgets for different expense categories
- **Transaction Tracking**: Record and categorize all your income and expenses
- **Visual Reports**: View spending trends and patterns with interactive charts
- **Monthly Analysis**: Review your financial data month by month
- **Responsive Design**: Works on desktop and mobile devices

## Tech Stack

### Backend
- Java 17 (Amazon Corretto)
- Spring Boot 3.1.5
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL database
- Flyway for database migrations
- Lombok for reducing boilerplate code
- Swagger/OpenAPI for API documentation

### Frontend
- React 18
- React Router for navigation
- Material UI for component library
- Axios for API communication
- Formik and Yup for form handling and validation
- MUI X Charts for data visualization
- Date-fns for date manipulation

### DevOps
- Docker and Docker Compose for containerization
- Nginx for serving the frontend and API routing

## Project Structure

```
budget-tracker/
├── budget-tracker-backend/     # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/budgettracker/  # Java source code
│   │   └── main/resources/
│   │       └── application.yml           # Application configuration
│   ├── Dockerfile              # Backend Docker configuration (Amazon Corretto)
│   └── pom.xml                 # Maven dependencies
├── budget-tracker-frontend/    # React frontend
│   ├── public/                 # Static assets
│   ├── src/                    # Source code
│   ├── Dockerfile              # Frontend Docker configuration
│   └── package.json            # NPM dependencies
└── docker-compose.yml          # Docker Compose configuration
```

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Node.js and npm (for local development)

### Running with Docker

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/budget-tracker.git
   cd budget-tracker
   ```

2. Start the application using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - API Documentation: http://localhost:8080/api/swagger-ui.html

### Local Development

#### Backend

1. Navigate to the backend directory:
   ```bash
   cd budget-tracker-backend
   ```

2. Install dependencies and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Frontend

1. Navigate to the frontend directory:
   ```bash
   cd budget-tracker-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

## API Documentation

The API documentation is available at `/api/swagger-ui.html` when the application is running.

## Database Schema

The application uses the following main entities:
- User: Stores user information and authentication details
- Currency: Defines supported currencies and their properties
- Category: Organizes expenses into categories and subcategories
- Budget: Defines spending limits for categories in specific time periods
- Transaction: Records all financial transactions

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [Material UI](https://mui.com/)
- [Docker](https://www.docker.com/)