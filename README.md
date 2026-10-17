# E-Commerce Backend System

A RESTful E-Commerce Backend Application developed using Java, Spring Boot, Spring Security, JWT Authentication, Spring Data JPA, and MySQL.

The application supports user authentication, product management, shopping cart operations, order placement, order history, and order cancellation.

## Technologies Used

- Java 21
- Spring Boot 4.0.8
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- MySQL 8
- Maven
- Postman
- Eclipse IDE

## Features

### User Authentication
- User registration
- Secure login using JWT
- Password encryption using BCrypt
- Role-based authorization (ADMIN and CUSTOMER)

### Product Management
- Add products (ADMIN)
- View products
- Search products
- Update products (ADMIN)
- Delete products (ADMIN)

### Shopping Cart
- Add products to cart
- View cart items
- Update product quantities
- Remove products from cart
- Stock availability validation

### Order Management
- Place orders through checkout
- Automatically calculate total amount
- Update product stock after checkout
- View order history
- View individual order details
- Cancel orders
- Restore product stock after cancellation

### Security
- JWT-based authentication
- Role-based access control
- BCrypt password hashing
- Protected customer and admin endpoints

## REST API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register user |
| POST | /api/auth/login | Login and receive JWT |

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/products | Get all products |
| GET | /api/products/{id} | Get product by ID |
| GET | /api/products/search?name= | Search products |
| POST | /api/products | Create product (ADMIN) |
| PUT | /api/products/{id} | Update product (ADMIN) |
| DELETE | /api/products/{id} | Delete product (ADMIN) |

### Shopping Cart

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/cart | Add product to cart |
| GET | /api/cart | View cart |
| PUT | /api/cart/{cartItemId} | Update quantity |
| DELETE | /api/cart/{cartItemId} | Remove item |

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/orders/checkout | Place order |
| GET | /api/orders | View order history |
| GET | /api/orders/{orderId} | View order details |
| PUT | /api/orders/{orderId}/cancel | Cancel order |

### User

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users/me | View logged-in user |

## Database

Database name: ecommerce_db

Main tables:
- users
- products
- cart_items
- orders
- order_items

## Configuration

The application uses environment variables for sensitive information.

Required environment variables:

- DB_PASSWORD
- JWT_SECRET
- SPRING_PROFILES_ACTIVE=dev

JWT_SECRET must be a Base64-encoded secret containing at least 32 random bytes.

Configure MySQL locally and create the database:

```sql
CREATE DATABASE ecommerce_db;
```

The application uses port 8083.

## Running the Application

1. Clone the repository.
2. Open the project in Eclipse or another Java IDE.
3. Install Java 21 and MySQL 8.
4. Configure the required environment variables.
5. Run EcommerceBackendApplication.java.
6. Test the APIs using Postman.

## API Authentication

Protected endpoints require a JWT token.

Authorization header:

```text
Authorization: Bearer <your_jwt_token>
```

## Future Improvements

- Payment gateway integration
- Product categories
- Pagination and sorting
- Automated unit and integration tests
- Improved concurrency handling for inventory
- Production deployment configuration

## Author

Nagalli Siddeswara

Java Backend Developer