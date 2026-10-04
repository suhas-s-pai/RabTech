# Task 05 - Spring Security 6 with JWT Token Authentication

## 1. Objective
The goal of Task 05 is to build a production-grade, stateless authentication and role-based authorization system using **Spring Security 6**, **BCrypt password hashing**, and **JSON Web Tokens (JWT)** in Spring Boot.

---

## 2. Technologies Used
- **Java 25**
- **Spring Boot 3.5.6**
- **Spring Security 6**
- **Spring Data JPA & Hibernate**
- **H2 In-Memory Database**
- **JJWT Library (v0.12.6)** for JWT generation & parsing
- **BCryptPasswordEncoder** for secure password hashing
- **Jakarta Validation**
- **JUnit 5 & Mockito** for automated unit & integration testing

---

## 3. Project Structure

```
task-05-spring-security-jwt/
├── pom.xml
├── README.md
├── postman/
│   └── RabTech-Task05-JWT.postman_collection.json
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── rabtech/
    │   │           └── task05/
    │   │               ├── Task05Application.java
    │   │               ├── config/
    │   │               │   └── SecurityConfig.java
    │   │               ├── controller/
    │   │               │   ├── AuthController.java
    │   │               │   └── SecureController.java
    │   │               ├── dto/
    │   │               │   ├── RegisterRequest.java
    │   │               │   ├── LoginRequest.java
    │   │               │   └── AuthResponse.java
    │   │               ├── entity/
    │   │               │   ├── User.java
    │   │               │   └── Role.java
    │   │               ├── repository/
    │   │               │   └── UserRepository.java
    │   │               ├── security/
    │   │               │   ├── JwtService.java
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   ├── CustomUserDetailsService.java
    │   │               │   ├── CustomAuthenticationEntryPoint.java
    │   │               │   └── CustomAccessDeniedHandler.java
    │   │               └── service/
    │   │                   └── AuthService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            └── com/
                └── rabtech/
                    └── task05/
                        ├── Task05ApplicationTests.java
                        ├── security/
                        │   └── JwtServiceTest.java
                        ├── service/
                        │   └── AuthServiceTest.java
                        └── controller/
                            └── SecurityIntegrationTest.java
```

---

## 4. How to Run & Test

### Run Unit and Integration Tests:
```powershell
.\mvnw.cmd -f task-05-spring-security-jwt/pom.xml clean test
```

### Start the Application:
```powershell
.\mvnw.cmd -f task-05-spring-security-jwt/pom.xml spring-boot:run
```
The application will start on `http://localhost:8081`.

---

## 5. Endpoints Overview

### Public Endpoints
- `POST /api/auth/register` – Register a new user (`USER` or `ADMIN`).
- `POST /api/auth/login` – Authenticate user and receive a JWT bearer token.

### Protected Endpoints
- `GET /api/secure/user` – Requires valid JWT with `USER` or `ADMIN` role.
- `GET /api/admin/dashboard` – Requires valid JWT with `ADMIN` role (returns `403 Forbidden` for `USER` role).

---

## 6. Example Requests & Responses

### Registration
`POST /api/auth/register`
```json
{
  "username": "suhas",
  "password": "password123",
  "role": "USER"
}
```
**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "suhas",
  "role": "USER",
  "message": "User registered successfully"
}
```

### Login
`POST /api/auth/login`
```json
{
  "username": "suhas",
  "password": "password123"
}
```
**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "suhas",
  "role": "USER",
  "message": "Login successful"
}
```

### Accessing User Endpoint
`GET /api/secure/user`  
Header: `Authorization: Bearer <your_token>`

**Response (200 OK):**
```json
{
  "message": "You are authenticated",
  "username": "suhas",
  "authorities": ["ROLE_USER"]
}
```

---

## 7. Security Architecture Highlights
1. **BCrypt Password Hashing**: Passwords are saved into the database strictly as salted BCrypt hashes (`$2a$10$...`).
2. **Stateless JWT Sessions**: `SessionCreationPolicy.STATELESS` ensures no HTTP sessions are created or persisted on the server.
3. **Custom Authentication Entry Point & Access Denied Handler**: Unauthenticated (401) and Unauthorized (403) errors return structured JSON instead of HTML pages.
4. **JWT Authentication Filter**: Intercepts every incoming request, validates the `Bearer <token>` header using `JwtService`, and populates `SecurityContextHolder`.
