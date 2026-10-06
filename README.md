# TicketDaata Microservices Setup

This project implements a microservice architecture for TicketDaata with the following services.

## Run it on Kubernetes

The whole system (frontend, gateway, all services, MongoDB, RabbitMQ) deploys to a local [kind](https://kind.sigs.k8s.io/) cluster with plain manifests and autoscaling — see **[`k8s/README.md`](k8s/README.md)** for the full walkthrough, and the architecture note below on why Eureka was replaced with Kubernetes-native service discovery for that deployment.

## Security note

Earlier revisions of this repo had a real MongoDB Atlas username/password committed in plaintext in `AuthService`, `OrdersService`, and `ticketservice`'s `application.yml`. Those are gone from the current tree (config is now env-var driven, see each service's `application.yml` and the `k8s/` Secrets), but removing them from the tree doesn't erase git history — if you fork this or reuse that Atlas cluster, rotate the credential.

## Services

### Service Registry (legacy, not deployed)

- **Location**: `ServiceRegistry/`
- **Purpose**: Eureka server for service discovery — kept in the repo as a record of the original design, but no longer part of the running system. Discovery is Kubernetes-native now (or plain `localhost` URLs for non-k8s local dev); see the Architecture section below.

### 1. API Gateway (Port: 9003)

- **Location**: `APIGateway/`
- **Purpose**: Routes requests to appropriate microservices
- **URL**: http://localhost:9003

### 2. Auth Service (Port: 9001)

- **Location**: `AuthService/`
- **Purpose**: JWT-based authentication and authorization with MongoDB persistence
- **Database**: MongoDB (local/in-cluster by default; override `MONGODB_URI` for Atlas or anywhere else)
- **URL**: http://localhost:9001

### 3. Orders Service (Port: 9002)

- **Location**: `OrdersService/`
- **Purpose**: Order management with lifecycle states, temporary reservations, and TTL-based expiration
- **Database**: MongoDB (local/in-cluster by default; override `MONGODB_URI` for Atlas or anywhere else)
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

### 1. Start Auth Service

```bash
cd AuthService
./mvnw spring-boot:run
```

### 2. Start Orders Service

```bash
cd OrdersService
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
Frontend (React + Vite)
    ↓
API Gateway (Port: 9003)
    ↓
┌─────────────────┬─────────────────┬─────────────────┐
│   Auth Service  │  Orders Service │ Ticket Service  │
│   (Port: 9001)  │  (Port: 9002)   │  (Port: 8082)   │
└─────────────────┴─────────────────┴─────────────────┘
```

Service-to-service calls use plain `http://<service-name>:<port>` URLs. Locally that's `localhost`; on Kubernetes it's the Service's DNS name, resolved by kube-dns — no separate discovery mechanism needed. `ServiceRegistry/` (a Eureka server) still exists in this repo as a record of the original design, but nothing registers to it anymore; see [`k8s/README.md`](k8s/README.md#architecture-change-eureka--kubernetes-native-discovery) for why it was dropped.

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

- Java 17
- Maven 3.6+
- A local MongoDB and RabbitMQ (or override `MONGODB_URI`/`RABBITMQ_*` to point elsewhere)

## Technology Stack

- **Spring Boot 3.5.3**
- **Spring Cloud 2025.0.0**
- **Spring Security**
- **Spring Data MongoDB**
- **Spring Cloud Gateway**
- **JWT (JSON Web Tokens)**
- **MongoDB Atlas** (Database)
- **Maven** (Build Tool)
