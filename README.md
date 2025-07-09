# E-Shop Project

## Overview
This is a sample **e-shop web application** built with Java and Spring Boot. It demonstrates a basic online store backend, including user registration and login, product management, shopping cart, order checkout, and integration with a payment gateway. The project follows a typical RESTful API design and layered architecture.

## Features
- **User Authentication:** Users can register an account and log in. Authentication is handled via JWT (JSON Web Tokens). Passwords are securely hashed (BCrypt).
- **Product Catalog:** Authenticated admins can create new products (with image uploaded to AWS S3), and any user can view product listings or details. Products have fields like name, description, price, category, and image URL.
- **Shopping Cart:** Each logged-in user has a cart. Users can add products to their cart (with specified quantity), view cart contents, and remove items. The cart is stored in MongoDB and tied to the user’s ID.
- **Order Processing:** Users can place an order for the items in their cart. The order is saved with a total amount and user shipping details. The system integrates with **GoPay** (a payment gateway) to initiate payment. Order status and payment status are tracked. After payment, the order is marked as paid and the user’s cart is cleared.
- **Email Notifications:** When an order is successfully paid, the system sends a confirmation email to the user (using Spring Mail).
- **Monitoring & Logging:** The application includes metrics (via Micrometer/Prometheus) for monitoring (e.g., number of logins, emails sent) and uses centralized logging/tracing (Grafana Loki and Tempo integration).
- **API Documentation:** The API is documented using OpenAPI/Swagger. You can explore the endpoints via Swagger UI once the app is running.

## Tech Stack
- Java 21, Spring Boot 3.5.0 (Web, Security, Data MongoDB, Validation)
- MongoDB for the primary database (product, user, order, cart data)
- JWT for security (spring security)
- AWS S3 for storing product images
- GoPay API for payment integration
- Redis (currently optional) for caching or future use
- Docker and Docker Compose for containerized deployment of the app and supporting services (MongoDB, Redis, Prometheus, Grafana, etc.)
- JUnit + Spring Boot Test for unit/integration tests

## Project Structure
```
src/main/java/com.lumastyle.eshop
 ├── controller       # REST controllers for HTTP requests (AuthController, ProductController, etc.)
 ├── service          # Service interfaces
 ├── service/impl     # Service implementations (ProductServiceImpl, OrderServiceImpl, etc.)
 ├── repository       # MongoDB repositories
 ├── entity           # MongoDB document models
 ├── dto              # Data Transfer Objects (requests/responses)
 ├── mapper           # MapStruct mappers
 ├── config           # Configuration classes (Security, CORS, OpenAPI, AWS S3)
 ├── exception        # Custom exceptions and global exception handler
 └── util             # Utility classes (JwtUtil, etc.)
src/test/java         # Test classes for controllers, services, utils
api-requests/         # Example HTTP requests for manual testing (IntelliJ HTTP Client)
infrastructure/monitoring/ # Prometheus, Grafana, Loki, Tempo configurations
```

## How to Run (Development)

### Prerequisites
- Java 17+
- Maven
- MongoDB and Redis (can be run via Docker Compose)

### Database Setup
If not using Docker, install MongoDB locally on the default port (27017). Update `spring.data.mongodb.uri` in `application.properties` if needed.

### Build
Compile and run tests using Maven:

```bash
./mvnw clean install
```

### Run Application
Start a Spring Boot application:

```bash
./mvnw spring-boot:run
```

The app runs on port configured in `application.properties` (default 8080 for HTTP, 8443 for HTTPS if TLS configured). You should see startup logs indicating a successful DB connection and initialization.

### Try the API
Navigate to Swagger UI:

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- or [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Use `.http` files or any API client (Postman, curl) to test endpoints. For protected endpoints, get JWT via login and include it in `Authorization` header.

## How to Run with Docker Compose
Fill in environment variables in `.env` file. Then run:

```bash
docker-compose up --build
```

This will start:
- Spring Boot app (`app` service)
- MongoDB database (with admin user from `.env`)
- Mongo Express (http://localhost:8081) for DB UI
- Redis
- Prometheus (http://localhost:9090)
- Grafana (http://localhost:3000, default admin/admin or per `.env`)
- Loki, Promtail, Tempo for logging & tracing

## Configuration

Sensitive data is set via environment variables or `.env` file:

- **MongoDB:** `MONGO_INITDB_ROOT_USERNAME`, `MONGO_INITDB_ROOT_PASSWORD`, `MONGO_INITDB_DATABASE`
- **JWT:** `JWT_SECRET_KEY`, `JWT_EXPIRATION_MILLIS`
- **AWS S3:** `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AWS_REGION`, `AWS_S3_BUCKET_NAME`
- **GoPay API:** `GOPAY_CLIENT_ID`, `GOPAY_CLIENT_SECRET`, `GOPAY_API_URL`, `GOPAY_GO_ID`, `GOPAY_CALLBACK_RETURN_URL`, `GOPAY_CALLBACK_NOTIFY_URL`
- **Email (SMTP):** `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- **Others:** `GF_SECURITY_ADMIN_PASSWORD` for Grafana

Ensure to replace placeholders with real or test values.

## Using the API

### Register
```http
POST /api/register
Content-Type: application/json

{
  "fullName": "...",
  "email": "...",
  "password": "..."
}
```

### Login
```http
POST /api/login
Content-Type: application/json

{
  "email": "...",
  "password": "..."
}
```
Returns JWT token – include as:

```
Authorization: Bearer <token>
```

### View Products
```http
GET /api/products
```

### Add Product (admin only)
```bash
curl -X POST http://localhost:8080/api/products   -H "Authorization: Bearer <ADMIN_TOKEN>"   -F 'product={ "name":"Sample","description":"Desc","price":100,"category":"Misc" };type=application/json'   -F "file=@path/to/image.png"
```

### Shopping Cart
- **Add:** `POST /api/cart` with JSON `{"productId": "<id>", "quantity": 2}`
- **View:** `GET /api/cart`
- **Remove:** `DELETE /api/cart/{productId}`

### Checkout Order
```http
POST /api/orders
Content-Type: application/json

{
  // order request body with delivery details
}
```

### Payment Verification
After GoPay payment, a callback notifies backend. For testing, simulate via:

```http
POST /api/orders/verify
```

### Order History
- **User:** `GET /api/orders`
- **Admin all:** `GET /api/orders/all`
- **Change status:** `PUT /api/orders/status/{orderId}`

## Running Tests
Run with:

```bash
mvn test
```

Tests use `application-test.properties` (local MongoDB expected). Placeholder SMTP (localhost:1025) recommended (MailHog). External integrations use placeholder or sandbox credentials.

## Known Issues or Future Improvements
- Role-based access control (currently all authenticated users can perform admin actions)
- Product updates not implemented yet
- Inventory management missing
- No frontend – backend only (testable via API)

---

