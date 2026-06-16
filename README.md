# Keycloak Gateway

A Spring Boot WebFlux application that provides a reactive gateway for Keycloak authentication and user management. This service acts as an intermediary between client applications and Keycloak, offering simplified REST API endpoints for user operations.

## Features

- 🔐 **Admin Authentication** - Secure admin login with Keycloak master realm
- 👤 **User Management** - Complete user lifecycle operations (register, update, delete)
- 🔑 **Authentication** - User login and token management
- ✅ **Token Validation** - Validate and introspect access tokens
- 🚪 **Logout** - Secure user logout functionality
- 🔄 **Reactive Architecture** - Built with Spring WebFlux for non-blocking operations
- 📊 **Automatic Logging** - Integrated logging aspect for request/response tracking
- 🐳 **Docker Support** - Containerized deployment ready

## Technology Stack

- **Java 21** - Latest LTS version with virtual threads support
- **Spring Boot 4.0.2** - Latest Spring framework
- **Spring WebFlux** - Reactive programming model
- **Keycloak** - Identity and access management
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management and build tool
- **Docker** - Container platform

## Prerequisites

Before running this application, ensure you have:

- **Java 21** or higher installed
- **Maven 3.9+** installed
- **Docker** and **Docker Compose** installed
- **Keycloak Server** running (can be deployed with Docker)

## Quick Start with Docker

### 1. Build the Docker Image

```bash
docker build -t keycloak-gw:latest .
```

### 2. Run with Docker

```bash
docker run -p 9090:9090 \
  -e KEYCLOAK_BASE_URL=http://keycloak:8080 \
  keycloak-gw:latest
```

### 3. Using Docker Compose (Recommended)

Create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    command: start-dev
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8080:8080"
    networks:
      - keycloak-network

  keycloak-gw:
    build: .
    ports:
      - "9090:9090"
    environment:
      KEYCLOAK_BASE_URL: http://keycloak:8080
    depends_on:
      - keycloak
    networks:
      - keycloak-network

networks:
  keycloak-network:
    driver: bridge
```

Then run:

```bash
docker-compose up -d
```

## Local Development Setup

### 1. Clone the Repository

```bash
git clone https://github.com/hoang170702/keycloak-gw.git
cd keycloak-gw
```

### 2. Configure Keycloak

#### Option A: Run Keycloak with Docker

```bash
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

#### Option B: Download and Run Keycloak Locally

1. Download Keycloak from [keycloak.org](https://www.keycloak.org/downloads)
2. Extract and start:

```bash
cd keycloak-{version}
bin/kc.sh start-dev  # Linux/Mac
# or
bin/kc.bat start-dev  # Windows
```

### 3. Configure Keycloak Realm and Client

1. Access Keycloak Admin Console at `http://localhost:8080`
2. Login with admin credentials (default: admin/admin)
3. Create a new realm named `ecom`
4. Create a client named `ecom-client`:
   - Client ID: `ecom-client`
   - Client Protocol: `openid-connect`
   - Access Type: `confidential`
   - Standard Flow Enabled: ON
   - Direct Access Grants Enabled: ON
   - Valid Redirect URIs: `/*`
5. Go to the Credentials tab and copy the client secret
6. Update `src/main/resources/application.yml` with your client secret
7. Create realm roles (e.g., `CUSTOMER`, `ADMIN`)

### 4. Build the Application

```bash
./mvnw clean package -DskipTests
# or on Windows
mvnw.cmd clean package -DskipTests
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
# or
java -jar target/keycloak-gw-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:9090`

## Configuration

The main configuration file is located at `src/main/resources/application.yml`:

```yaml
server:
  port: 9090

spring:
  application:
    name: keycloak-gw
  threads:
    virtual:
      enabled: true

keycloak:
  base-url: http://localhost:8080
  master:
    admin-realm: master
    admin-client-id: admin-cli
  ecom:
    ecom-realm: ecom
    ecom-client-id: ecom-client
    ecom-client-secret: YOUR_CLIENT_SECRET_HERE
```

### Environment Variables

You can override configuration using environment variables:

```bash
export SERVER_PORT=9090
export KEYCLOAK_BASE_URL=http://localhost:8080
export KEYCLOAK_ECOM_CLIENT_SECRET=your-secret
```

## API Documentation

### Base URL

```
http://localhost:9090/api/v1
```

### Authentication Endpoints

#### 1. Admin Login

**Endpoint:** `POST /auth/admin/login`

**Description:** Authenticate as Keycloak admin

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 300,
    "refreshExpiresIn": 1800,
    "tokenType": "Bearer"
  }
}
```

### User Management Endpoints

#### 2. Register User

**Endpoint:** `POST /users/register`

**Description:** Create a new user in Keycloak

**Headers:**
- `Authorization: Bearer {admin_token}`

**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "enabled": true,
  "emailVerified": true,
  "credentials": [
    {
      "type": "password",
      "value": "SecurePass123!",
      "temporary": false
    }
  ],
  "requiredActions": []
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": {
    "userId": "user-uuid",
    "username": "john.doe",
    "message": "User created successfully"
  }
}
```

#### 3. User Login

**Endpoint:** `POST /users/login`

**Description:** Authenticate a user and get access token

**Request Body:**
```json
{
  "username": "john.doe",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 300,
    "refreshExpiresIn": 1800,
    "tokenType": "Bearer"
  }
}
```

#### 4. Get User Details

**Endpoint:** `GET /users/{userId}`

**Description:** Retrieve user information by ID

**Headers:**
- `Authorization: Bearer {admin_token}`

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": {
    "id": "user-uuid",
    "username": "john.doe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "emailVerified": true,
    "createdTimestamp": 1234567890
  }
}
```

#### 5. Update User

**Endpoint:** `PUT /users/{userId}`

**Description:** Update user information

**Headers:**
- `Authorization: Bearer {admin_token}`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": "User updated successfully"
}
```

#### 6. Reset Password

**Endpoint:** `PUT /users/{userId}/reset-password`

**Description:** Update user password

**Headers:**
- `Authorization: Bearer {admin_token}`

**Request Body:**
```json
{
  "password": "NewSecurePass123!",
  "temporary": false
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": "Password updated successfully"
}
```

#### 7. Validate Token

**Endpoint:** `POST /users/validate-token`

**Description:** Validate and introspect an access token

**Request Body:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIs..."
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": {
    "active": true,
    "exp": 1234567890,
    "iat": 1234567590,
    "username": "john.doe",
    "email": "john.doe@example.com",
    "scope": "profile email"
  }
}
```

#### 8. Logout

**Endpoint:** `POST /users/logout`

**Description:** Logout user and invalidate refresh token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "status": "SUCCESS",
  "data": "Logged out successfully"
}
```

## Testing with Postman

A Postman collection is included in the project at `src/main/resources/keycloak-gw.postman_collection.json`.

### Import the Collection

1. Open Postman
2. Click **Import** button
3. Select the file `src/main/resources/keycloak-gw.postman_collection.json`
4. The collection will be imported with all API endpoints

### Using the Collection

1. First, run the **admin-login** request to get an admin token
2. Copy the `accessToken` from the response
3. Use this token in the `Authorization` header for protected endpoints
4. For user operations, use the **user-login** request to get user tokens

## Project Structure

```
keycloak-gw/
├── src/
│   ├── main/
│   │   ├── java/ecom/keycloakgw/
│   │   │   ├── KeycloakGwApplication.java
│   │   │   ├── api/
│   │   │   │   ├── controller/          # REST Controllers
│   │   │   │   └── exception/           # Exception handlers
│   │   │   ├── application/
│   │   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── service/             # Service interfaces
│   │   │   │   └── validator/           # Request validators
│   │   │   ├── domain/
│   │   │   │   └── utils/               # Domain utilities
│   │   │   └── infrastructure/
│   │   │       ├── config/              # Configuration classes
│   │   │       ├── filter/              # WebFlux filters
│   │   │       └── service/             # Service implementations
│   │   └── resources/
│   │       ├── application.yml          # Application configuration
│   │       ├── logback-spring.xml       # Logging configuration
│   │       └── keycloak-gw.postman_collection.json
│   └── test/                            # Test files
├── Dockerfile                           # Docker build configuration
├── pom.xml                              # Maven dependencies
└── README.md                            # This file
```

## Error Handling

The API uses a standardized error response format:

```json
{
  "requestId": "uuid",
  "status": "ERROR",
  "message": "Error description",
  "errors": [
    {
      "field": "fieldName",
      "message": "Validation error message"
    }
  ]
}
```

## Logging

The application uses Logback for logging with automatic request/response logging through AOP aspects.

Log files location: Check `logback-spring.xml` for configuration

Log levels can be configured in `application.yml`:

```yaml
logging:
  level:
    ecom.keycloakgw: DEBUG
    org.springframework: INFO
```

## Building for Production

### Build JAR

```bash
./mvnw clean package -DskipTests
```

The JAR file will be created at `target/keycloak-gw-0.0.1-SNAPSHOT.jar`

### Build Docker Image

```bash
docker build -t keycloak-gw:1.0.0 .
```

### Deploy to Production

Ensure you:
1. Update `application.yml` with production Keycloak URL
2. Use environment variables for sensitive data
3. Configure proper logging levels
4. Set up monitoring and health checks
5. Use HTTPS for all communications

## Health Check

Spring Boot Actuator endpoints (if enabled):

```bash
curl http://localhost:9090/actuator/health
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Troubleshooting

### Common Issues

**1. Application fails to start**
- Verify Keycloak is running and accessible
- Check if port 9090 is available
- Verify Java 21 is installed

**2. Authentication fails**
- Verify Keycloak realm and client configuration
- Check client secret in `application.yml`
- Ensure user exists in Keycloak

**3. Connection refused to Keycloak**
- Verify Keycloak base URL in configuration
- Check network connectivity
- Ensure Keycloak is fully started

**4. Docker build fails**
- Ensure `.m2/repository` directory exists with `logging-common` dependency
- Check Docker daemon is running
- Verify Dockerfile syntax

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please open an issue on GitHub.

## Acknowledgments

- Spring Boot Team
- Keycloak Community
- All contributors to this project