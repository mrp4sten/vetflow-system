package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Priority;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Type;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.flyway.locations=classpath:testdb/migration",
    "spring.datasource.url=jdbc:h2:mem:vetflow;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class AppointmentJpaRepositoryTest {

  @Autowired
  OwnerJpaRepository owners;
  @Autowired
  PatientJpaRepository patients;
  @Autowired
  AppointmentJpaRepository appointments;

  private PatientEntity newPatient() {
    OwnerEntity o = new OwnerEntity();
    o.setName("Owner");
    o.setEmail("owner@vetflow.com");
    o.setPhone("+525500000000");
    o.setAddress("CDMX");
    OwnerEntity savedOwner = owners.saveAndFlush(o);

    PatientEntity p = new PatientEntity();
    p.setName("Firulais");
    p.setSpecies("dog");
    p.setWeight(new BigDecimal("10.00"));
    p.setOwner(savedOwner);
    return patients.saveAndFlush(p);
  }

  @Test
  @DisplayName("Saves appointment and queries by patient/status/date range")
  void savesAndQueries() {
    PatientEntity p = newPatient();

    AppointmentEntity a = new AppointmentEntity();
    a.setPatient(p);
    a.setAppointmentDate(LocalDateTime.of(2025, 1, 10, 10, 0));
    a.setType(Type.CHECKUP);
    a.setStatus(Status.SCHEDULED);
    a.setPriority(Priority.NORMAL);
    a.setNotes("first");
    AppointmentEntity saved = appointments.saveAndFlush(a);

    assertThat(saved.getId()).isNotNull();

    List<AppointmentEntity> byPatient = appointments.findByPatientId(p.getId());
    assertThat(byPatient).extracting(AppointmentEntity::getNotes).contains("first");

    List<AppointmentEntity> byStatus = appointments.findByStatus(Status.SCHEDULED);
    assertThat(byStatus).isNotEmpty();

    List<AppointmentEntity> byRange = appointments.findByAppointmentDateBetween(
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 31, 23, 59));
    assertThat(byRange).hasSizeGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Rejects appointment without patient (FK) or without date/type (NOT NULL)")
  void rejectsMissingFields() {
    AppointmentEntity a = new AppointmentEntity();
    a.setType(Type.CHECKUP);
    a.setStatus(Status.SCHEDULED);
    a.setPriority(Priority.NORMAL);
    // faltan patient y appointmentDate
    assertThatThrownBy(() -> appointments.saveAndFlush(a))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Autowired
  JdbcTemplate jdbc;

  @Test
  @DisplayName("Persists enums as lowercase strings")
  void persistsEnumsAsLowercase() {
    OwnerEntity owner = new OwnerEntity();
    owner.setName("O");
    owner.setEmail("o@v.com");
    owner.setPhone("+5255000");
    owner.setAddress("X");
    owners.saveAndFlush(owner);

    PatientEntity p = new PatientEntity();
    p.setName("Firu");
    p.setSpecies("dog");
    p.setOwner(owner);
    patients.saveAndFlush(p);

    AppointmentEntity a = new AppointmentEntity();
    a.setPatient(p);
    a.setAppointmentDate(LocalDateTime.now().plusDays(1));
    a.setType(AppointmentEntity.Type.CHECKUP);
    a.setStatus(AppointmentEntity.Status.SCHEDULED);
    a.setPriority(AppointmentEntity.Priority.NORMAL);
    appointments.saveAndFlush(a);

    String typeDb = jdbc.queryForObject("select type from appointments where id = ?",
        String.class, a.getId());
    String statusDb = jdbc.queryForObject("select status from appointments where id = ?",
        String.class, a.getId());
    String priorityDb = jdbc.queryForObject("select priority from appointments where id = ?",
        String.class, a.getId());

    assertThat(typeDb).isEqualTo("checkup");
    assertThat(statusDb).isEqualTo("scheduled");
    assertThat(priorityDb).isEqualTo("normal");
  }

  @Test
  @DisplayName("created_at is auto-filled")
  void createdAtAutofilled() {
    OwnerEntity owner = new OwnerEntity();
    owner.setName("O");
    owner.setEmail("o@v.com");
    owner.setPhone("+5255000");
    owner.setAddress("X");
    owners.saveAndFlush(owner);

    PatientEntity p = new PatientEntity();
    p.setName("Firu");
    p.setSpecies("dog");
    p.setOwner(owner);
    patients.saveAndFlush(p);

    AppointmentEntity a = new AppointmentEntity();
    a.setPatient(p);
    a.setAppointmentDate(LocalDateTime.now().plusDays(1));
    a.setType(AppointmentEntity.Type.CHECKUP);
    appointments.saveAndFlush(a);

    AppointmentEntity reloaded = appointments.findById(a.getId()).orElseThrow();
    assertThat(reloaded.getCreatedAt()).isNotNull();
  }

}
