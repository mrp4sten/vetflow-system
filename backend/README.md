# VetFlow API

Modern veterinary clinic backend built with Spring Boot 3.5, Spring Security 6, Flyway and PostgreSQL.  
This document summarizes how to run, test and consume the API for portfolio/demo purposes.

## Quick Start

### 1. Run with Maven

```bash
cd backend
./mvnw spring-boot:run
```

### 2. Run with Docker Compose

```bash
cd docker
docker compose up --build
```

Compose launches both `postgres` and `vetflow_api`. Edit `docker/.env` to change database credentials, JWT secret or allowed origins before starting the stack.

## Configuration

| Purpose | Property | Default |
|---------|----------|---------|
| Postgres URL | `spring.datasource.url` | `jdbc:postgresql://localhost:5432/vetflow` |
| JWT Secret | `vetflow.api.security.jwt.secret` | `change-me-in-prod-32-char-secret!` |
| JWT TTL | `vetflow.api.security.jwt.expiration` | `PT1H` |
| CORS | `vetflow.api.cors.allowed-origins` | `http://localhost:3000,http://localhost:4200` |

Override any property via environment variables (e.g. `VETFLO_API_SECURITY_JWT_SECRET`).

## API Documentation (Swagger / OpenAPI)

1. Start the backend.
2. Browse to `http://localhost:8080/swagger-ui/index.html`.
3. Click **Authorize** and paste `Bearer <jwt token>` (keep the `Bearer` prefix).
4. Explore endpoints by tags (Appointments, Patients, Owners, Medical Records, Authentication).  
   Each endpoint includes a curated summary/description plus the request/response schemas.

Raw OpenAPI JSON is always available at `http://localhost:8080/v3/api-docs` and can be imported into Postman, Bruno, Insomnia or used for client code generation.

## Authentication Flow

Seed at least one system user:

```sql
INSERT INTO system_users (username, email, password_hash, role, is_active)
VALUES (
  'admin',
  'admin@vetflow.test',
  '$2b$10$RDWS5TPPOW5J6iKIHOScI.5JgQZd/yHkSXOcQMuBHyyPwx52n6dd6', -- password: Vetflow#2024
  'admin',
  true
);
```

(Generate custom bcrypt hashes with `mkpasswd -m bcrypt 'your-password'`.)

Then request a token:

```bash
API=http://localhost:8080/api/v1
TOKEN=$(curl -s -X POST "$API/auth/token" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"Vetflow#2024"}' | jq -r '.accessToken')
echo "JWT: $TOKEN"
```

## cURL Collection

```bash
# Create owner
curl -X POST "$API/owners" -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Laura Garcia","phone":"+1-555-0100","email":"laura@example.com","address":"742 Evergreen"}'

# Register patient
curl -X POST "$API/patients" -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Rex","species":"DOG","breed":"Labrador","birthDate":"2018-05-10","ownerId":1}'

# Schedule appointment
curl -X POST "$API/appointments" -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"appointmentDate":"2024-12-05T10:30:00","type":"CHECKUP","priority":"NORMAL","notes":"Annual visit"}'

# Create medical record
curl -X POST "$API/medical-records" -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"veterinarianId":1,"visitDate":"2024-12-05T10:30:00","diagnosis":"Allergies"}'
```

Import any of the commands above (or `/v3/api-docs`) into Postman/Bruno for a richer client experience.

## Testing

```bash
./mvnw test              # full suite
./mvnw -Dtest=OpenApiSmokeTest test   # Swagger smoke test only
```

CI-safe coverage reports land in `backend/target/site/jacoco`.

## Useful Scripts

- `scripts/setup-env.sh` – interactive helper that exports DB, Sonar and JWT settings locally.
- `scripts/githooks/*` – optional hooks for linting/formatting before commits.

## Deployment Notes

- The Docker image (`backend/Dockerfile`) is multi-stage: Maven builds the jar, a slim Temurin 21 JRE runs it.
- When deploying to cloud providers, set `VETFLO_API_SECURITY_JWT_SECRET` and `SPRING_DATASOURCE_*` via managed secrets.
- Expose Swagger UI only in trusted environments or gate it behind auth if hosting publicly.
