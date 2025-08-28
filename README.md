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
- **Purpose**: JWT-based authentication and authorization with MongoDB persistence
- **Database**: MongoDB Atlas
- **URL**: http://localhost:9001

### 4. Orders Service (Port: 9002)

- **Location**: `OrdersService/`
- **Purpose**: Order management with lifecycle states, temporary reservations, and TTL-based expiration
- **Database**: MongoDB Atlas
- **URL**: http://localhost:9002
- **Features**:
  - Order creation and management
  - Automatic order expiration (15 minutes TTL)
  - Order lifecycle states (PENDING, COMPLETED, CANCELLED, EXPIRED)
  - Duplicate order prevention
  - Payment integration support

## Getting Started

### Quick Start (All Services)

Run all services at once using the batch script:

```bash
.\start-services.bat
```

This will start all services in the correct order with appropriate delays.

### Manual Start (Individual Services)

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

### 3. Start Orders Service

```bash
cd OrdersService
./mvnw spring-boot:run
```

### 4. Start API Gateway

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

### Orders Endpoints (via API Gateway)

- **POST** `/orders` - Create new order
- **GET** `/orders/{orderId}` - Get order by ID
- **PUT** `/orders/{orderId}` - Update order
- **POST** `/orders/{orderId}/complete` - Complete order
- **POST** `/orders/{orderId}/cancel` - Cancel order
- **GET** `/orders/user/{userId}/pending/count` - Get pending orders count
- **GET** `/orders/payment/{paymentId}` - Get order by payment ID
- **GET** `/orders/health` - Service health check

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

#### Create Order

```json
POST http://localhost:9003/orders
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
    "userId": "user123",
    "ticketId": "ticket456",
    "ticketTitle": "Concert Ticket",
    "eventName": "Rock Concert 2024",
    "quantity": 2,
    "unitPrice": 50.00
}
```

## Future Services to Implement

1. **User Service** - User profile management
2. **Ticket Service** - Ticket booking and management
3. **Event Service** - Event creation and management
4. **Payment Service** - Payment processing integration
5. **Notification Service** - Email and SMS notifications

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

- **Auth Service**: MongoDB Atlas (Cloud Database)
  - Database: `ticketdaata_auth`
  - Collection: `users`
  - Connection configured via MongoDB URI in `application.yml`

### MongoDB Configuration

The Auth Service uses MongoDB Atlas for user data persistence. To set up:

1. **MongoDB Atlas Setup**:

   - Create a MongoDB Atlas account at https://www.mongodb.com/atlas
   - Create a new cluster
   - Get your connection string

2. **Update Configuration**:

   - Update the MongoDB URI in `AuthService/src/main/resources/application.yml`:

   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?retryWrites=true&w=majority&appName=<appName>
         database: ticketdaata_auth
   ```

3. **User Document Structure**:
   ```json
   {
     "_id": "ObjectId",
     "username": "string",
     "email": "string",
     "password": "string (encrypted)",
     "role": "USER|ADMIN"
   }
   ```

## JWT Configuration

- Secret key is configured in each service's `application.yml`
- Token expiration: 24 hours
- Tokens include username and role information

## Security Features

- **Password Encryption**: BCrypt password encoding
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **Role-based Authorization**: USER and ADMIN roles
- **MongoDB Integration**: Secure user data persistence

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB Atlas account (for Auth Service)
- Internet connection (for Eureka service discovery)

## Technology Stack

- **Spring Boot 3.5.3**
- **Spring Cloud 2025.0.0**
- **Spring Security**
- **Spring Data MongoDB**
- **Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway**
- **JWT (JSON Web Tokens)**
- **MongoDB Atlas** (Database)
- **Maven** (Build Tool)
