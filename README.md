# 🚀 TaskFlow REST API

> 👨‍💻 **Designed & Developed by:** **Islam Ahmed Ramadan**  
> 🎯 **Specialization:** Backend Java & Spring Boot Developer

TaskFlow API is a production-ready, secure task management backend built with **Spring Boot / Java 17**, **PostgreSQL**, **Spring Security with JWT**, **OpenAPI / Swagger**, and **Docker**.

---

## 🌟 Key Features

1. **Authentication & Authorization (JWT):**
   - Stateless authentication with JSON Web Tokens.
   - Role-based security filters protecting business endpoints.

2. **Task & Project Management:**
   - Full CRUD operations with Pagination & Sorting (`Pageable`).
   - Task priorities (`LOW`, `MEDIUM`, `HIGH`) and statuses (`TODO`, `IN_PROGRESS`, `DONE`).
   - DTO pattern with MapStruct mapping.

3. **Secure File Uploads & Attachments:**
   - Strict pattern validation using optimized Regex (`^[a-zA-Z0-9_\s.-]+\.(jpg|jpeg|png|pdf|docx|txt)$`).
   - Secure UUID-based file storage preventing path traversal and name collisions.

4. **Comments & Notifications:**
   - Comment system for collaborative tasks.
   - Decoupled notification architecture.

5. **Observability & Logging:**
   - Correlation ID tracking via MDC filter across request lifecycles.
   - Production monitoring via **Spring Boot Actuator** (`/actuator/health`, `/actuator/metrics`).

6. **Interactive API Documentation:**
   - OpenAPI 3.0 / Swagger UI with integrated JWT Bearer authentication.

7. **Automated Testing:**
   - Unit tests for services with **JUnit 5** and **Mockito**.
   - API Integration tests with `@SpringBootTest` and `MockMvc`.

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL
- **Security:** Spring Security, JJWT
- **Mapping & Boilerplate:** MapStruct, Lombok
- **Testing:** JUnit 5, Mockito, MockMvc
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Containerization:** Docker & Docker Compose

---

## 🚀 Quick Start with Docker (Recommended)

To run the entire system (Spring Boot API + PostgreSQL) with a single command:

```bash
docker compose up --build
```

- API will be accessible at: `http://localhost:8080`
- Swagger UI will be accessible at: `http://localhost:8080/swagger-ui/index.html`
- Health check: `http://localhost:8080/actuator/health`

To stop the containers:
```bash
docker compose down
```

---

## 💻 Local Development Setup (Without Docker)

### Prerequisites:
- JDK 17 or higher
- PostgreSQL running on `localhost:5432` with database `taskflow_db`

### Run Application:
```bash
# Windows
.\mvnw spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

### Run Tests:
```bash
# Windows
.\mvnw test

# Linux / Mac
./mvnw test
```

---

## 📖 How to Use Swagger UI

1. Open your browser to: **`http://localhost:8080/swagger-ui/index.html`**
2. **Register a User:**
   - Expand `auth-controller` ➡️ `POST /api/auth/register`
   - Click **Try it out** and execute with sample user data.
3. **Login & Get Token:**
   - Expand `POST /api/auth/login`
   - Execute with your email and password. Copy the returned `token` string.
4. **Authorize:**
   - Click the **Authorize 🔒** button at the top right.
   - Paste the token into the value box and click **Authorize**.
5. **Test Protected Endpoints:**
   - You can now test `POST /api/tasks`, `GET /api/tasks`, and attachment uploads directly from the browser!

---

## 📊 Monitoring Endpoints

- **Health Check:** `GET /actuator/health`
- **Application Info:** `GET /actuator/info`
- **Application Metrics:** `GET /actuator/metrics`

---

## 📁 Project Structure

```text
TaskFlow-API/
├── src/
│   ├── main/
│   │   ├── java/com/example/TaskFlow/API/
│   │   │   ├── config/          # OpenAPI & App configurations
│   │   │   ├── controller/      # REST API Controllers
│   │   │   ├── dto/             # Request & Response DTOs
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── mapper/          # MapStruct Mappers
│   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   ├── security/        # JWT Filter, Utility & SecurityConfig
│   │   │   └── service/         # Business Logic Layer
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit & Integration Tests
├── Dockerfile                   # Multi-stage Docker build
├── docker-compose.yml           # Multi-container orchestration (App + DB)
├── pom.xml                      # Maven configuration
└── README.md                    # Project documentation
```

---

## 👨‍💻 Author & Developer

**Islam Ahmed Ramadan**  
*Backend Java & Spring Boot Developer*

- 💼 **Domain:** Backend Development (Java / Spring Boot / Microservices Architecture)
- 🌐 **Portfolio Project:** TaskFlow RESTful API Engine

