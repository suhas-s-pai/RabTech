# Teamo — Team Task Management Platform

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.5.6](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security 6](https://img.shields.io/badge/Spring%20Security-6-green.svg)](https://spring.io/projects/spring-security)
[![React 18](https://img.shields.io/badge/React-18.3-blue.svg)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-6.0-purple.svg)](https://vitejs.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue.svg)](https://supabase.com/)

**Teamo** is a modern, full-stack enterprise Team Task Management and Code Review Platform built for RabTech Academy Internship Task 06. It streamlines task delegation, code submissions, and review workflows between **Managers** and **Employees** with real-time status tracking, security enforcement, and interactive dashboards.

---

## 🚀 Key Features

### 👨‍💼 Manager Portal
- **Create & Assign Tasks**: Managers can create tasks with title, description, deadline, and assign them directly to registered employees.
- **Manager Dashboard**: Real-time stats (Total Created, Pending Review, In Progress, Approved).
- **Code Review & Feedback**: Inspect employee GitHub repository submissions and submission comments.
- **Decision Engine**: Approve submitted work or request changes with detailed manager feedback comments.

### 👷 Employee Portal
- **Workspace Dashboard**: Track assigned tasks, in-progress items, pending reviews, and approved deliverables.
- **Workflow State Engine**: Transition tasks through lifecycle:
  `ASSIGNED` ➔ `IN_PROGRESS` ➔ `SUBMITTED` ➔ `APPROVED` or `CHANGES_REQUESTED`
- **GitHub Submission**: Submit completed work with GitHub repository URLs and notes.
- **Revision Handling**: Re-start and re-submit tasks when changes are requested by managers.

### 🔒 Security & Architecture
- **Stateless JWT Authentication**: Secure JSON Web Tokens with `Bearer` authentication header.
- **BCrypt Password Encryption**: All passwords stored securely as salted BCrypt hashes (`$2a$10$...`).
- **Role-Based Authorization (RBAC)**: Endpoint-level security (`hasRole('MANAGER')`, `hasRole('EMPLOYEE')`).
- **No Password Leaks**: All user payloads are converted into safe `UserDto` objects excluding password fields.
- **Structured Error Handling**: Custom JSON 401 Unauthorized and 403 Forbidden handlers plus `@RestControllerAdvice`.
- **OpenAPI 3.0 / Swagger UI**: Built-in interactive API documentation at `/swagger-ui.html`.

---

## 🛠️ Tech Stack

- **Backend**: Java 25, Spring Boot 3.5.6, Spring Security 6, Spring Data JPA, Hibernate, Jakarta Validation
- **Database**: H2 (Development & Test), PostgreSQL / Supabase (Production)
- **Frontend**: React 18, Vite 6, Axios, Lucide Icons, CSS3 (Modern Flex/Grid Layouts)
- **API Documentation**: Springdoc OpenAPI v2.8.5 (Swagger UI)
- **Build Tools**: Maven Wrapper (`mvnw`), Node.js (npm)

---

## 📁 Project Structure

```
task-06-teamo/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/teamo/backend/
│       │   │   ├── BackendApplication.java
│       │   │   ├── config/ (SecurityConfig, SwaggerConfig, GlobalExceptionHandler, DataInitializer)
│       │   │   ├── controller/ (AuthController, TaskController, UserController)
│       │   │   ├── dto/ (UserDto, AuthRequest, AuthResponse, RegisterRequest, TaskCreateRequest, TaskSubmitRequest, TaskReviewRequest, TaskResponse)
│       │   │   ├── entity/ (User, Task, Role, TaskStatus)
│       │   │   ├── repository/ (UserRepository, TaskRepository)
│       │   │   ├── security/ (JwtService, JwtAuthenticationFilter, CustomUserDetailsService, CustomAuthenticationEntryPoint, CustomAccessDeniedHandler)
│       │   │   └── service/ (AuthService, TaskService, UserService)
│       │   └── resources/
│       │       ├── application.properties (Default H2)
│       │       └── application-postgres.properties (PostgreSQL / Supabase)
│       └── test/ (Unit & TaskWorkflowIntegrationTest)
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── api/ (Axios JWT Interceptor)
│       ├── context/ (AuthContext)
│       ├── components/ (Navbar, StatCard, TaskCard, StatusBadge, Modals)
│       └── pages/ (LoginPage, ManagerDashboard, EmployeeDashboard)
└── README.md
```

---

## ⚡ Quick Start Guide

### 1. Prerequisites
- **JDK 25** installed
- **Node.js (v18+)** & **npm** installed

---

### 2. Running the Backend

Navigate to backend directory:
```powershell
cd task-06-teamo/backend
```

Run tests:
```powershell
.\mvnw.cmd clean test
```

Start the Spring Boot backend server:
```powershell
.\mvnw.cmd spring-boot:run
```
The backend starts on `http://localhost:8080`.

Interactive Swagger API docs available at:
`http://localhost:8080/swagger-ui.html`

---

### 3. Running the React Frontend

Open a new terminal and navigate to frontend directory:
```powershell
cd task-06-teamo/frontend
```

Install dependencies:
```powershell
npm install
```

Build for production:
```powershell
npm run build
```

Start Vite dev server:
```powershell
npm run dev
```
The frontend starts on `http://localhost:3000`.

---

## 👥 Instant Demo Accounts (Pre-Seeded)

The backend auto-seeds the following demo accounts upon startup for rapid evaluation:

| User | Role | Email | Password |
| :--- | :--- | :--- | :--- |
| **Krishna** | **MANAGER** | `krishna@rabtech.com` | `password123` |
| **Suhas** | **EMPLOYEE** | `suhas@rabtech.com` | `password123` |

*Single-click quick login buttons are available on the frontend login page!*

---

## 🔄 Complete Task Lifecycle Workflow

```
[Manager Creates Task] ➔ ASSIGNED
       │
       ▼
[Employee Starts Task] ➔ IN_PROGRESS
       │
       ▼
[Employee Submits Code Link] ➔ SUBMITTED
       │
   ┌───┴────────────────────────┐
   ▼                            ▼
[Manager Approves] ➔ APPROVED   [Manager Requests Changes] ➔ CHANGES_REQUESTED ➔ (Re-Start & Re-Submit)
```

---

## 🌐 Production Database Configuration (PostgreSQL / Supabase)

To connect Teamo to PostgreSQL / Supabase, pass environment variables or activate `postgres` profile:

```powershell
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres --SPRING_DATASOURCE_URL=jdbc:postgresql://db.supabase.co:5432/postgres --SPRING_DATASOURCE_USERNAME=postgres --SPRING_DATASOURCE_PASSWORD=your_password
```

