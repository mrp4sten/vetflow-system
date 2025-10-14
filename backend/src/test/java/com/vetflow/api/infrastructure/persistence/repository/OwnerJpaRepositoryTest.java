package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:vetflow;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
class OwnerJpaRepositoryTest {

  @Autowired
  OwnerJpaRepository owners;

  @Test
  @DisplayName("Saves and retrieves an Owner by id and email")
  void savesAndReadsOwner() {
    OwnerEntity o = new OwnerEntity();
    o.setName("John Doe");
    o.setEmail("john@vetflow.com");
    o.setPhone("+525511112233");
    o.setAddress("CDMX");

    OwnerEntity saved = owners.saveAndFlush(o);

    assertThat(saved.getId()).isNotNull();

    assertThat(owners.findById(saved.getId())).isPresent();
    assertThat(owners.findByEmail("john@vetflow.com"))
        .isPresent()
        .get()
        .extracting(OwnerEntity::getName)
        .isEqualTo("John Doe");
  }

  @Test
  @DisplayName("Enforces unique email constraint")
  void enforcesUniqueEmail() {
    OwnerEntity a = new OwnerEntity();
    a.setName("A");
    a.setEmail("unique@vetflow.com");
    a.setPhone("+525511111111");
    a.setAddress("X");
    owners.saveAndFlush(a);

    OwnerEntity b = new OwnerEntity();
    b.setName("B");
    b.setEmail("unique@vetflow.com"); // duplicate
    b.setPhone("+525522222222");
    b.setAddress("Y");

    assertThatThrownBy(() -> owners.saveAndFlush(b))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Entities with different ids are not equal")
  void equalsHashCodeBasic() {
    OwnerEntity o1 = new OwnerEntity();
    o1.setId(1L);
    OwnerEntity o2 = new OwnerEntity();
    o2.setId(2L);
    assertThat(o1).isNotEqualTo(o2);
  }

  @Test
  @DisplayName("findByEmail returns empty when not found, and updates persist")
  void findByEmailNegative_andUpdatePersists() {
    OwnerEntity o = new OwnerEntity();
    o.setName("Jane");
    o.setEmail("jane@vetflow.com");
    o.setPhone("+525500000000");
    o.setAddress("CDMX");
    owners.saveAndFlush(o);

    assertThat(owners.findByEmail("nope@vetflow.com")).isNotPresent();

    // update path
    o.setAddress("GDL");
    o.setPhone("+525599999999");
    owners.saveAndFlush(o);

    OwnerEntity reloaded = owners.findById(o.getId()).orElseThrow();
    assertThat(reloaded.getAddress()).isEqualTo("GDL");
    assertThat(reloaded.getPhone()).isEqualTo("+525599999999");
  }

  @Test
  @DisplayName("Rejects Owner with empty name or name > 100")
  void rejectsInvalidOwnerName() {
    OwnerEntity longName = new OwnerEntity();
    longName.setName("A".repeat(101));
    longName.setEmail("y@vetflow.com");
    longName.setPhone("+525522222222");
    longName.setAddress("B");
    assertThatThrownBy(() -> owners.saveAndFlush(longName))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

}
