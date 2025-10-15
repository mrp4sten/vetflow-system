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
import org.springframework.test.context.TestPropertySource;

import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;
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
class MedicalRecordJpaRepositoryTest {

  @Autowired OwnerJpaRepository owners;
  @Autowired PatientJpaRepository patients;
  @Autowired MedicalRecordJpaRepository records;

  private PatientEntity newPatient() {
    OwnerEntity o = new OwnerEntity();
    o.setName("Owner");
    o.setEmail("owner@vetflow.com");
    o.setPhone("+525500000000");
    o.setAddress("CDMX");
    OwnerEntity savedOwner = owners.saveAndFlush(o);

    PatientEntity p = new PatientEntity();
    p.setName("Mimi");
    p.setSpecies("cat");
    p.setWeight(new BigDecimal("4.00"));
    p.setOwner(savedOwner);
    return patients.saveAndFlush(p);
  }

  @Test
  @DisplayName("Saves medical record and queries by patient ordered by visit date DESC")
  void savesAndFindsByPatient() {
    PatientEntity p = newPatient();

    MedicalRecordEntity r1 = new MedicalRecordEntity();
    r1.setPatient(p);
    r1.setVeterinarianId(1L);
    r1.setVisitDate(LocalDateTime.of(2025,1,1,9,0));
    r1.setDiagnosis("Dermatitis");
    r1.setNotes("Topical treatment");
    records.saveAndFlush(r1);

    MedicalRecordEntity r2 = new MedicalRecordEntity();
    r2.setPatient(p);
    r2.setVeterinarianId(1L);
    r2.setVisitDate(LocalDateTime.of(2025,2,1,9,0));
    r2.setDiagnosis("Otitis");
    r2.setNotes("Antibiotics");
    records.saveAndFlush(r2);

    List<MedicalRecordEntity> list = records.findByPatientIdOrderByVisitDateDesc(p.getId());
    assertThat(list).hasSize(2);
    assertThat(list.get(0).getDiagnosis()).isEqualTo("Otitis"); // más reciente primero
  }

  @Test
  @DisplayName("Rejects record without patient or diagnosis")
  void rejectsInvalidRecord() {
    MedicalRecordEntity r = new MedicalRecordEntity();
    r.setVeterinarianId(1L);
    r.setVisitDate(LocalDateTime.now());
    r.setDiagnosis(null); // NOT NULL + CHECK en DB
    assertThatThrownBy(() -> records.saveAndFlush(r))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
