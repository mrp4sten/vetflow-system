package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

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
class PatientJpaRepositoryTest {

  @Autowired
  OwnerJpaRepository owners;
  @Autowired
  PatientJpaRepository patients;

  private OwnerEntity newOwner() {
    OwnerEntity o = new OwnerEntity();
    o.setName("Owner");
    o.setEmail("owner@vetflow.com");
    o.setPhone("+525500000000");
    o.setAddress("CDMX");
    return owners.saveAndFlush(o);
  }

  @Test
  @DisplayName("Saves a Patient linked to an Owner and can query by ownerId")
  void savesPatientWithOwner_andQueryByOwner() {
    OwnerEntity owner = newOwner();

    PatientEntity p = new PatientEntity();
    p.setName("Firulais");
    p.setSpecies("dog");
    p.setBreed("Mestizo");
    p.setBirthDate(LocalDate.of(2020, 1, 1));
    p.setWeight(new BigDecimal("12.30"));
    p.setOwner(owner);

    PatientEntity saved = patients.saveAndFlush(p);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getOwner().getId()).isEqualTo(owner.getId());

    List<PatientEntity> byOwner = patients.findByOwnerId(owner.getId());
    assertThat(byOwner).extracting(PatientEntity::getName).contains("Firulais");
  }

  @Test
  @DisplayName("Rejects Patient without Owner (FK constraint)")
  void rejectsPatientWithoutOwner() {
    PatientEntity p = new PatientEntity();
    p.setName("Solo");
    p.setSpecies("dog");
    p.setWeight(new BigDecimal("5.00"));
    // no owner set

    assertThatThrownBy(() -> patients.saveAndFlush(p))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Rejects negative weight (CHECK constraint)")
  void rejectsNegativeWeight() {
    OwnerEntity owner = newOwner();
    PatientEntity p = new PatientEntity();
    p.setName("PesoNegativo");
    p.setSpecies("dog");
    p.setWeight(new BigDecimal("-1.00"));
    p.setOwner(owner);

    assertThatThrownBy(() -> patients.saveAndFlush(p))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Rejects null name and null species (NOT NULL constraints)")
  void rejectsNullNameAndNullSpecies() {
    OwnerEntity owner = newOwner();

    // name null
    PatientEntity p1 = new PatientEntity();
    p1.setName(null); // NOT NULL
    p1.setSpecies("dog");
    p1.setOwner(owner);
    p1.setWeight(new BigDecimal("1.00"));
    assertThatThrownBy(() -> patients.saveAndFlush(p1))
        .isInstanceOf(DataIntegrityViolationException.class);

    // species null
    PatientEntity p2 = new PatientEntity();
    p2.setName("NoSpecies");
    p2.setSpecies(null); // NOT NULL
    p2.setOwner(owner);
    p2.setWeight(new BigDecimal("1.00"));
    assertThatThrownBy(() -> patients.saveAndFlush(p2))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Rejects breed longer than 100 characters")
  void rejectsBreedTooLong() {
    OwnerEntity owner = newOwner();

    PatientEntity p = new PatientEntity();
    p.setName("LongBreed");
    p.setSpecies("dog");
    p.setOwner(owner);
    p.setWeight(new BigDecimal("3.00"));

    // 101 chars
    p.setBreed("A".repeat(101));

    assertThatThrownBy(() -> patients.saveAndFlush(p))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

}
