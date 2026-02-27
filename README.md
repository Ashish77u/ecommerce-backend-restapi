# E-Commerce Backend Application

A full-stack e-commerce-backend application built with Java, Spring Boot, Spring Security, JWT, MySQL.

## 🚀 Features

- **User Authentication & Authorization** (JWT-based)
- **Role-Based Access Control** (USER/ADMIN)
- **Product & Category Management**
- **Shopping Cart & Order Management**
- **Payment Processing** (Mocked Gateway)
- **Stock Management** with automatic rollback on payment failure
- **RESTful API** with proper exception handling

[//]: # (- **Responsive UI** with Thymeleaf + Bootstrap 5)

---

## 🛠️ Tech Stack

**Backend:**
- Spring Boot 4.0.2 , also work on (3.4.2)
- Spring Security 7.x (JWT Authentication)
- Spring Data JPA
- MySQL 8.0
- Maven

[//]: # (**Frontend:**)
[//]: # (- Thymeleaf)
[//]: # (- Bootstrap 5)
[//]: # (- JavaScript &#40;Vanilla&#41;)

---

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- IDE (IntelliJ IDEA / Eclipse / VS Code)

---

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/ecommerce-backend.git
cd ecommerce-backend
```

### 2. Configure Database

Create a MySQL database:
```sql
CREATE DATABASE ecommerce_db;
```

### 3. Update Application Properties

Copy `application.properties.example` to `application.properties`:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` and update:
```properties
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
jwt.secret=YOUR_SECURE_JWT_SECRET_KEY
```

### 4. Build the Project
```bash
./mvnw clean install
```

### 5. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start at `http://localhost:8080`

---

## 📁 Project Structure
```
ecommerce-backend/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── config/           # Security, JWT config
│   │   │   ├── controller/       # REST 
│   │   │   ├── dto/              # Request/Response DTOs
│   │   │   ├── entity/           # JPA Entities
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── repository/       # JPA Repositories
│   │   │   ├── security/         # JWT utilities
│   │   │   └── service/          # Business logic
│   │   └── resources/
│   │       └── application.properties.example
│   └── test/                     # Unit tests
├── pom.xml
├── README.md
└── .gitignore
```

---

## 🔑 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user (returns JWT token)
- `GET /api/auth/me` - Get current user info

### Products (Public)
- `GET /api/products` - Get all products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{categoryId}` - Get products by category
- `GET /api/products/search?keyword={keyword}` - Search products

### Products (Admin Only)
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders (Authenticated Users)
- `POST /api/orders` - Create new order
- `GET /api/orders/my-orders` - Get user's orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/cancel` - Cancel order

### Payments
- `POST /api/payments` - Process payment for order
- `GET /api/payments/order/{orderId}` - Get payment details

---

## 👤 Default Users

After running the application, you can create users via `/register` or use SQL to create an admin:
```sql
-- Create admin user (password: Admin@123)
INSERT INTO users (username, email, password, full_name, role, is_active, is_email_verified, created_at, updated_at)
VALUES (
    'admin',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin User',
    'ADMIN',
    1,
    1,
    NOW(),
    NOW()
);
```

---

## 🧪 Testing

### Using Postman

1. Register a user: `POST /api/auth/register`
2. Login: `POST /api/auth/login` (copy JWT token)
3. Set Authorization header: `Bearer YOUR_JWT_TOKEN`
4. Test protected endpoints

[//]: # (### Using Browser)
[//]: # ()
[//]: # (1. Navigate to `http://localhost:8080`)
[//]: # (2. Click "Register" and create account)
[//]: # (3. Login and browse products)
[//]: # (4. Create orders and test payment flow)

---

## 🔒 Security Features

- Password encryption with BCrypt
- JWT token-based authentication
- Role-based authorization (USER/ADMIN)
- CSRF protection disabled (stateless REST API)
- Secure password validation (min 8 chars, uppercase, lowercase, digit)

---

## 📝 Database Schema

Key entities:
- **users** - User accounts with roles
- **categories** - Product categories
- **products** - Product catalog with stock
- **orders** - Customer orders with status tracking
- **order_items** - Order line items (product snapshots)
- **payments** - Payment transactions

---

## 🎦 Screenshort 

<img width="1917" height="762" alt="auth 1" src="https://github.com/user-attachments/assets/5b7741f8-a34f-42b5-aee4-61afc58060f8" />
<img width="1918" height="567" alt="category 2" src="https://github.com/user-attachments/assets/a461b339-a782-4eb8-8825-3f2ff11a4db0" />
<img width="1918" height="552" alt="product 3" src="https://github.com/user-attachments/assets/31fe4665-e00d-4d95-bc54-5868c95d5d35" />
<img width="1918" height="287" alt="payment 5" src="https://github.com/user-attachments/assets/250db122-15be-4310-8a14-55848ecbe9ce" />
<img width="1918" height="556" alt="order 4" src="https://github.com/user-attachments/assets/a691797a-b3db-4564-ab84-503be5c3505e" />



## 👨‍💻 Author

**Your Name**
- GitHub: [@Ashish77u](https://github.com/Ashish77u)
- Email: ashishs77u@gmail.com

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- Baeldung Tutorials
- Stack Overflow Community

---

## 📞 Support

For questions or support, please open an issue in the GitHub repository.
