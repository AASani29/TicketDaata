# TicketDaata Microservices Setup

This project implements a microservice architecture for TicketDaata with the following services:

## Services

### 1. Service Registry (Port: 8761)

- **Location**: `ServiceRegistry/`
- **Purpose**: Eureka server for service discovery
- **URL**: http://localhost:8761

### 2. API Gateway (Port: 9003)

- **Location**: `APIGateway/`
- **Purpose**: Routes requests to appropriate microservices
- **URL**: http://localhost:9003

### 3. Auth Service (Port: 9001)

- **Location**: `AuthService/`
- **Purpose**: JWT-based authentication and authorization
- **URL**: http://localhost:9001

## Getting Started

### 1. Start Service Registry

```bash
cd ServiceRegistry
./mvnw spring-boot:run
```

### 2. Start Auth Service

```bash
cd AuthService
./mvnw spring-boot:run
```

### 3. Start API Gateway

```bash
cd APIGateway
./mvnw spring-boot:run
```

## API Endpoints

### Authentication Endpoints (via API Gateway)

- **POST** `/auth/register` - Register new user
- **POST** `/auth/login` - User login
- **POST** `/auth/logout` - User logout
- **POST** `/auth/validate` - Validate JWT token
- **GET** `/auth/username` - Extract username from token

### Sample Requests

#### Register User

```json
POST http://localhost:9003/auth/register
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
}
```

#### Login User

```json
POST http://localhost:9003/auth/login
Content-Type: application/json

{
    "username": "john_doe",
    "password": "password123"
}
```

## Future Services to Implement

1. **User Service** - User profile management
2. **Ticket Service** - Ticket booking and management
3. **Event Service** - Event creation and management

## Architecture

```
Frontend (React/Angular/Vue)
    ↓
API Gateway (Port: 9003)
    ↓
┌─────────────────┬─────────────────┬─────────────────┐
│   Auth Service  │  User Service   │ Ticket Service  │
│   (Port: 9001)  │  (Port: 9002)   │  (Port: 9004)   │
└─────────────────┴─────────────────┴─────────────────┘
    ↑
Service Registry (Port: 8761)
```

## Database

- **Auth Service**: H2 in-memory database (for development)
- **H2 Console**: http://localhost:9001/h2-console
  - JDBC URL: `jdbc:h2:mem:authdb`
  - Username: `sa`
  - Password: `password`

## JWT Configuration

- Secret key is configured in each service's `application.yml`
- Token expiration: 24 hours
- Tokens include username and role information
