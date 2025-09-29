# News Management System - Backend API

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [System Requirements](#system-requirements)
5. [Installation & Setup](#installation--setup)
6. [Database Schema](#database-schema)
7. [API Endpoints](#api-endpoints)
8. [Authentication](#authentication)
9. [User Roles & Permissions](#user-roles--permissions)
10. [Testing with Postman](#testing-with-postman)
11. [Project Structure](#project-structure)
12. [Troubleshooting](#troubleshooting)

---

## Overview

A RESTful API backend for a News Management System with role-based access control. The system allows different user types (Normal users, Content Writers, and Admins) to interact with news articles based on their permissions.

**Key Capabilities:**
- User authentication with JWT tokens
- Role-based authorization
- News CRUD operations with approval workflow
- Automatic soft-deletion of expired news
- Multi-language support (English & Arabic)

---

## Features

### Authentication System
- User registration (signup) with email validation
- User login with JWT token generation
- Access token (1-minute expiration) and refresh token (24-hour expiration)
- Logout functionality
- Token refresh mechanism

### User Management (Admin Only)
- Create, Read, Update, Delete users
- Assign user roles (Normal, Writer, Admin)
- View user details and list all users

### News Management
- **Writers & Admins:** Create news articles (pending status)
- **Admins:** Approve or reject pending news
- **All Users:** Read approved news articles
- **Writers:** Delete own pending news
- **Admins:** Delete any news article
- Automatic soft-deletion of news past publish date

### Security Features
- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control
- Request validation
- CORS support

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 11+ |
| **Framework** | Spring Boot | 3.x |
| **Security** | Spring Security | 6.x |
| **Database** | MySQL | 8.0+ |
| **ORM** | Hibernate/JPA | - |
| **Authentication** | JWT (jjwt) | 0.11.5 |
| **Server** | Tomcat | 9.x |
| **Build Tool** | Maven | 3.6+ |
| **API Documentation** | Springdoc OpenAPI | 2.2.0 |

---

## System Requirements

### Prerequisites
- JDK 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Port 8090 available
- Postman (for API testing)

---

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd news_app
```

### 2. Configure Database

Create a MySQL database:
```sql
CREATE DATABASE news_app;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/news_app?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

server.port=8090
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration
bezkoder.app.jwtSecret=MySuperStrongJwtSecretKeyForMyApp12345678910111213---
bezkoder.app.jwtExpirationMs=60000
bezkoder.app.jwtRefreshExpirationMs=86400000
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8090`

### 5. Initialize Roles

Insert default roles into the database:
```sql
INSERT INTO roles (name) VALUES ('ROLE_NORMAL');
INSERT INTO roles (name) VALUES ('ROLE_WRITER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

---

## Database Schema

### Tables

#### `users`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(20) | UNIQUE, NOT NULL |
| email | VARCHAR(50) | UNIQUE, NOT NULL |
| password | VARCHAR(120) | NOT NULL |
| full_name | VARCHAR(100) | NOT NULL |
| date_of_birth | DATE | |

#### `roles`
| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(20) | ENUM: ROLE_NORMAL, ROLE_WRITER, ROLE_ADMIN |

#### `user_roles`
| Column | Type | Constraints |
|--------|------|-------------|
| user_id | BIGINT | FOREIGN KEY → users(id) |
| role_id | INT | FOREIGN KEY → roles(id) |

#### `news`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| title | VARCHAR(255) | NOT NULL |
| title_ar | VARCHAR(255) | NOT NULL |
| description | TEXT | NOT NULL |
| description_ar | TEXT | NOT NULL |
| publish_date | DATE | NOT NULL |
| image_url | VARCHAR(500) | |
| status | VARCHAR(20) | ENUM: PENDING, APPROVED, REJECTED |
| deleted | BOOLEAN | DEFAULT FALSE |
| user_id | BIGINT | FOREIGN KEY → users(id) |

---

## API Endpoints

### Authentication APIs

#### 1. User Registration
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "dateOfBirth": "1990-01-15",
  "role": ["normal"]
}
```

**Response (200 OK):**
```json
{
  "message": "User registered successfully!"
}
```

#### 2. User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_NORMAL"]
}
```

#### 3. Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

#### 4. Logout
```http
POST /api/auth/logout
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "message": "User logged out successfully!"
}
```

---

### User Management APIs (Admin Only)

#### 1. Create User
```http
POST /api/user
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "username": "writer1",
  "email": "writer@example.com",
  "password": "password123",
  "fullName": "Writer One",
  "dateOfBirth": "1995-05-20",
  "roles": ["ROLE_WRITER"]
}
```

#### 2. Get All Users
```http
GET /api/user
Authorization: Bearer <admin_token>
```

#### 3. Get User by ID
```http
GET /api/user/{id}
Authorization: Bearer <admin_token>
```

#### 4. Get User by Email
```http
GET /api/user/email/{email}
Authorization: Bearer <admin_token>
```

#### 5. Update User
```http
PUT /api/user/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "username": "updated_username",
  "email": "updated@example.com",
  "fullName": "Updated Name",
  "dateOfBirth": "1995-05-20",
  "roles": ["ROLE_WRITER", "ROLE_ADMIN"]
}
```

#### 6. Delete User
```http
DELETE /api/user/{id}
Authorization: Bearer <admin_token>
```

---

### News Management APIs

#### 1. Create News (Writer/Admin)
```http
POST /api/news
Authorization: Bearer <writer_or_admin_token>
Content-Type: application/json

{
  "title": "Breaking News",
  "titleAr": "أخبار عاجلة",
  "description": "This is the news description",
  "descriptionAr": "هذا هو وصف الخبر",
  "publishDate": "2025-10-01",
  "imageUrl": "https://example.com/news-image.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Breaking News",
  "titleAr": "أخبار عاجلة",
  "description": "This is the news description",
  "descriptionAr": "هذا هو وصف الخبر",
  "publishDate": "2025-10-01",
  "imageUrl": "https://example.com/news-image.jpg",
  "status": "PENDING",
  "authorName": "writer1"
}
```

#### 2. Get All News (Admin Only)
```http
GET /api/news/all
Authorization: Bearer <admin_token>
```

#### 3. Get Approved News (All Authenticated Users)
```http
GET /api/news/approved
Authorization: Bearer <any_user_token>
```

#### 4. Get Pending News (Admin Only)
```http
GET /api/news/pending
Authorization: Bearer <admin_token>
```

#### 5. Update News (Writer/Admin)
```http
PUT /api/news/{id}
Authorization: Bearer <writer_or_admin_token>
Content-Type: application/json

{
  "title": "Updated Title",
  "titleAr": "عنوان محدث",
  "description": "Updated description",
  "descriptionAr": "وصف محدث",
  "publishDate": "2025-10-02",
  "imageUrl": "https://example.com/updated-image.jpg"
}
```

#### 6. Approve News (Admin Only)
```http
PUT /api/news/{id}/approve
Authorization: Bearer <admin_token>
```

#### 7. Reject News (Admin Only)
```http
PUT /api/news/{id}/reject
Authorization: Bearer <admin_token>
```

#### 8. Delete News (Writer/Admin)
```http
DELETE /api/news/{id}
Authorization: Bearer <writer_or_admin_token>
```

**Note:** Writers can only delete their own pending news. Admins can delete any news.

---

## Authentication

### JWT Token Flow

1. **Login:** User provides credentials → receives access token (1-minute TTL) and refresh token (24-hour TTL)
2. **API Access:** Include access token in Authorization header: `Bearer <token>`
3. **Token Expiry:** When access token expires, use refresh token to get new access token
4. **Logout:** Client deletes stored tokens

### Token Format
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## User Roles & Permissions

| Role | Permissions |
|------|-------------|
| **ROLE_NORMAL** | Login, Logout, Read approved news |
| **ROLE_WRITER** | All Normal permissions + Create news, Update own pending news, Delete own pending news |
| **ROLE_ADMIN** | All permissions including User CRUD, Approve/Reject news, Delete any news |

### Permission Matrix

| Endpoint | Normal | Writer | Admin |
|----------|--------|--------|-------|
| POST /api/auth/signup | ✅ | ✅ | ✅ |
| POST /api/auth/login | ✅ | ✅ | ✅ |
| POST /api/auth/logout | ✅ | ✅ | ✅ |
| POST /api/auth/refresh | ✅ | ✅ | ✅ |
| GET /api/news/approved | ✅ | ✅ | ✅ |
| POST /api/news | ❌ | ✅ | ✅ |
| PUT /api/news/{id} | ❌ | ✅ (own) | ✅ |
| DELETE /api/news/{id} | ❌ | ✅ (pending) | ✅ |
| GET /api/news/all | ❌ | ❌ | ✅ |
| GET /api/news/pending | ❌ | ❌ | ✅ |
| PUT /api/news/{id}/approve | ❌ | ❌ | ✅ |
| PUT /api/news/{id}/reject | ❌ | ❌ | ✅ |
| /api/user/** | ❌ | ❌ | ✅ |

---

## Testing with Postman

### 1. Import Environment Variables

Create a Postman environment with:
- `base_url`: `http://localhost:8090`
- `access_token`: (will be set automatically)
- `refresh_token`: (will be set automatically)

### 2. Test Sequence

**Step 1: Register Admin User**
```
POST {{base_url}}/api/auth/signup
Body: {
  "username": "admin",
  "email": "admin@test.com",
  "password": "admin123",
  "fullName": "Admin User",
  "dateOfBirth": "1990-01-01",
  "role": ["admin"]
}
```

**Step 2: Login**
```
POST {{base_url}}/api/auth/login
Body: {
  "username": "admin",
  "password": "admin123"
}

Tests Script:
pm.environment.set("access_token", pm.response.json().token);
pm.environment.set("refresh_token", pm.response.json().refreshToken);
```

**Step 3: Create News**
```
POST {{base_url}}/api/news
Headers: Authorization: Bearer {{access_token}}
Body: {
  "title": "Test News",
  "titleAr": "خبر تجريبي",
  "description": "Description",
  "descriptionAr": "وصف",
  "publishDate": "2025-10-01",
  "imageUrl": "http://example.com/image.jpg"
}
```

**Step 4: Approve News**
```
PUT {{base_url}}/api/news/1/approve
Headers: Authorization: Bearer {{access_token}}
```

**Step 5: Get Approved News**
```
GET {{base_url}}/api/news/approved
Headers: Authorization: Bearer {{access_token}}
```

---

## Project Structure

```
news_app/
├── src/
│   ├── main/
│   │   ├── java/com/global/news_app/
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── NewsController.java
│   │   │   ├── models/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── ERole.java
│   │   │   │   ├── News.java
│   │   │   │   └── NewsStatus.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── NewsRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   └── NewsService.java
│   │   │   ├── security/
│   │   │   │   ├── WebSecurityConfig.java
│   │   │   │   ├── jwt/
│   │   │   │   │   ├── JwtUtils.java
│   │   │   │   │   ├── AuthTokenFilter.java
│   │   │   │   │   └── AuthEntryPointJwt.java
│   │   │   │   └── services/
│   │   │   │       ├── UserDetailsImpl.java
│   │   │   │       └── UserDetailsServiceImpl.java
│   │   │   ├── payload/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── SignupRequest.java
│   │   │   │   │   ├── UserRequest.java
│   │   │   │   │   └── NewsRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── JwtResponse.java
│   │   │   │       ├── MessageResponse.java
│   │   │   │       ├── UserResponse.java
│   │   │   │       └── NewsResponse.java
│   │   │   ├── mapper/
│   │   │   │   ├── UserMapper.java
│   │   │   │   └── NewsMapper.java
│   │   │   ├── config/
│   │   │   │   └── SwaggerConfig.java
│   │   │   └── NewsAppApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

---

## Troubleshooting

### Common Issues

#### 1. **Database Connection Error**
```
Error: Communications link failure
```
**Solution:**
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure database `news_app` exists

#### 2. **JWT Token Expired**
```
Status: 401 Unauthorized
Message: JWT token is expired
```
**Solution:**
- Use the refresh token endpoint to get a new access token
- Access token expires in 1 minute (as per requirements)

#### 3. **403 Forbidden**
```
Status: 403 Forbidden
Message: Access Denied
```
**Solution:**
- Check user role has required permissions
- Verify JWT token is correctly included in Authorization header

#### 4. **Role Not Found Error**
```
Error: Role is not found
```
**Solution:**
- Ensure roles are inserted into the database:
```sql
INSERT INTO roles (name) VALUES ('ROLE_NORMAL'), ('ROLE_WRITER'), ('ROLE_ADMIN');
```

#### 5. **Port Already in Use**
```
Error: Port 8090 is already in use
```
**Solution:**
- Change port in `application.properties`: `server.port=8091`
- Or kill process using port 8090

---

## API Best Practices Implemented

- RESTful URL structure
- Proper HTTP methods (GET, POST, PUT, DELETE)
- Appropriate HTTP status codes
- JWT-based stateless authentication
- Input validation with `@Valid`
- Exception handling
- Soft deletion for data integrity
- CORS configuration
- Role-based access control

---

## License

© AppsWave 2024 - All Rights Reserved

---

## Contact & Support

For issues or questions, please contact the development team.

**Database Dump & Postman Collection:** Available upon request via email.
