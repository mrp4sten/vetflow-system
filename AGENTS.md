# AGENTS.md

## Project Overview

VetFlow is a veterinary clinic management system with a Spring Boot 3.5 backend (Java 17) using PostgreSQL. The frontend and mobile app are not yet implemented.

## Common Commands

### Backend Development (from `backend/` directory)

```bash
# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw -Dtest=PatientApplicationServiceTest test

# Run a specific test method
./mvnw -Dtest=PatientApplicationServiceTest#shouldRegisterPatient test

# Build with coverage report (output: target/site/jacoco/)
./mvnw verify

# Skip tests during build
./mvnw package -DskipTests
```

### Docker (from `docker/` directory)

```bash
# Start full stack (postgres + api)
docker compose up --build

# Start only database
docker compose up postgres
```

### Database Access

```bash
docker exec -it vetflow_db psql -U mrp4sten -d vetflow
```

## Architecture

The backend follows **Hexagonal Architecture** with **Domain-Driven Design**:

```
com.vetflow.api/
├── domain/           # Pure business logic (no JPA annotations)
│   ├── model/        # Rich domain models: Appointment, MedicalRecord, Owner, Patient
│   └── port/         # Repository interfaces (contracts)
├── application/      # Use case handlers and commands
│   ├── appointment/  # AppointmentApplicationService, ScheduleAppointmentCommand
│   ├── patient/      # PatientApplicationService, RegisterPatientCommand
│   ├── owner/        # OwnerApplicationService
│   ├── medicalrecord/
│   └── shared/       # ApplicationException, ResourceNotFoundException, ValidationException
├── infrastructure/persistence/
│   ├── adapter/      # Repository implementations (ports -> JPA)
│   ├── entity/       # JPA entities: AppointmentEntity, PatientEntity, etc.
│   ├── mapper/       # MapStruct: Domain <-> Entity conversion
│   └── repository/   # Spring Data JPA interfaces
├── security/
│   ├── config/       # SecurityConfig, SecurityProperties
│   ├── jwt/          # JwtTokenService, JwtAuthenticationFilter
│   └── user/         # SystemUserDetailsService
└── web/v1/           # REST controllers and DTOs
    ├── appointment/  # Request/Response DTOs
    ├── patient/
    ├── owner/
    ├── medicalrecord/
    ├── auth/
    └── error/        # GlobalExceptionHandler, ErrorResponse
```

**Key patterns:**
- Domain models contain business logic, no framework annotations
- Application services orchestrate use cases via Command objects
- Repository adapters bridge domain ports to JPA repositories
- MapStruct handles all domain-entity mapping

## Key Technologies

- **Spring Boot 3.5.6** with Spring Security 6 (JWT authentication)
- **Flyway** for database migrations (`src/main/resources/db/migration/`)
- **MapStruct** for entity mapping (configured in pom.xml compiler plugin)
- **Lombok** for boilerplate reduction
- **springdoc-openapi** for Swagger UI at `/swagger-ui/index.html`
- **TestContainers** + **H2** for integration tests
- **JaCoCo** for code coverage

## Configuration

Main config: `backend/src/main/resources/application.properties`

Key environment variables:
- `VETFLO_JWT_SECRET` - JWT signing key (required in production)
- `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` - Database connection
- `VETFLO_API_CORS_ALLOWED_ORIGINS` - CORS whitelist

## API Endpoints

All endpoints under `/api/v1/`:
- `POST /auth/token` - Get JWT token
- `/owners`, `/patients`, `/appointments`, `/medical-records` - CRUD resources

OpenAPI docs: `http://localhost:8080/v3/api-docs`

## Database

PostgreSQL 15 with Flyway migrations. Core tables:
- `system_users` - Authentication (roles: admin, veterinarian, assistant)
- `owners` - Pet owners
- `patients` - Pets with owner FK
- `appointments` - Scheduling with status/type/priority enums
- `medical_records` - Visit history
- `audit_logs` - Change tracking

Seed admin user (password: `Vetflow#2024`):
```sql
INSERT INTO system_users (username, email, password_hash, role, is_active)
VALUES ('admin', 'admin@vetflow.test', '$2b$10$RDWS5TPPOW5J6iKIHOScI.5JgQZd/yHkSXOcQMuBHyyPwx52n6dd6', 'admin', true);
```
