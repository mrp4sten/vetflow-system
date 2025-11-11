# Infrastructure Persistence Guide

This document describes how the VetFlow backend persistence layer is organized: JPA entities, MapStruct mappers, repository adapters, Flyway migrations, and how to run local/integration tests (Postgres + Testcontainers).

## 1. Package structure

```text
src/main/java/com.vetflow.api/infrastructure/persistence
├── entity/               # JPA entities (OwnerEntity, PatientEntity, AppointmentEntity, MedicalRecordEntity)
├── mapper/               # MapStruct mappers (OwnerMapper, PatientMapper, AppointmentMapper, MedicalRecordMapper)
├── repository/           # Spring Data JPA repositories
├── adapter/              # Adapters that implement domain repository ports
└── AbstractPostgresDataJpaTest.java  # base for integration tests with Postgres (Testcontainers)
```

### 1.1 JPA entities
- Mirror the tables from `src/main/resources/db/migration/V1__baseline.sql`.
- `@Id` is `Long` with identity.
- Timestamps: at least `created_at` → `LocalDateTime createdAt` filled in `@PrePersist`.
- FKs:
  - `patients.owner_id → owners.id`
  - `appointments.patient_id → patients.id` (CASCADE)
  - `medical_records.patient_id → patients.id`
  - `medical_records.veterinarian_id → system_users.id` (RESTRICT)

---

## 2. MapStruct mappers

All mappers are under `com.vetflow.api.infrastructure.persistence.mapper` and are annotated with `@Mapper(componentModel = "spring")`.

### 2.1 Lowercase mapping

We normalize enum/string-like fields to lowercase before persisting, because our Flyway SQL has CHECK constraints with uppercase or lowercase expectations depending on the table.

Example (simplified):

```java
@Mapper(componentModel = "spring", uses = { OwnerMapper.class })
public interface PatientMapper {
  @Mapping(target = "species", expression = "java(toDbSpecies(domain.getSpecies()))")
  PatientEntity toEntity(Patient domain);

  @Mapping(target = "species", expression = "java(toDomainSpecies(entity.getSpecies()))")
  Patient toDomain(PatientEntity entity);

  default String toDbSpecies(String species) {
    return species == null ? null : species.toLowerCase(java.util.Locale.ROOT);
  }

  default String toDomainSpecies(String db) {
    return db == null ? null : db.toLowerCase(java.util.Locale.ROOT);
  }
}
```

We applied the same idea for Appointment (type/status/priority) and for MedicalRecord (trim or null).

---

## 3. Repository adapters

Domain layer defines ports. Infra layer implements them by:
1. injecting the Spring Data JPA repository,
2. injecting the MapStruct mapper,
3. converting domain ↔ entity.

Annotate adapters with `@Component` and mark write methods transactional.

---

## 4. Flyway migrations

Config in `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
```

- Hibernate does **not** create the schema.
- Flyway always runs first.
- UTC everywhere.

For tests we keep a mirrored SQL in `src/test/resources/testdb/migration/...` so H2 can start with the same structure.

---

## 5. Testing strategy

### 5.1 Fast H2 tests
- Use `@DataJpaTest`.
- H2 in PostgreSQL mode.
- Flyway test migration applied.
- Validate constraints and repository queries.

### 5.2 Testcontainers + Postgres
- Use an abstract base test annotated with `@Testcontainers`.
- Start `postgres:16`.
- Provide JDBC properties through `@DynamicPropertySource`.
- Flyway runs V1 against the container.
- Run CRUD + repository query tests (Owner, Patient, Appointment, MedicalRecord).
- For MedicalRecord we insert test rows into `system_users` first because of the FK.

---

## 6. Local setup

### 6.1 Run Postgres with Docker

```bash
docker run --name vetflow-pg   -e POSTGRES_USER=vetflow   -e POSTGRES_PASSWORD=vetflow   -e POSTGRES_DB=vetflow   -p 5432:5432   -d postgres:16
```

### 6.2 Env vars

```bash
export DB_URL=jdbc:postgresql://localhost:5432/vetflow
export DB_USERNAME=vetflow
export DB_PASSWORD=vetflow
```

### 6.3 Run app/tests

```bash
mvn clean test
mvn spring-boot:run
```

For coverage:

```bash
mvn clean verify
open target/site/jacoco/index.html
```

---

## 7. Tips

- If you see “missing table …” in a test: your test Flyway SQL is missing the table. Add it to `src/test/resources/testdb/migration/V1__baseline.sql`.
- If Postgres tests fail with FK error on `medical_records.veterinarian_id`, insert a dummy `system_users` row in the test via `EntityManager` before persisting the record.
- Keep enums/strings normalized in the mappers so that DB CHECK constraints continue to pass.
