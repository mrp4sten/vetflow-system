package com.vetflow.api.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;
import com.vetflow.api.infrastructure.persistence.repository.OwnerJpaRepository;
import com.vetflow.api.infrastructure.persistence.repository.PatientJpaRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaSmokeTest {

    @Autowired OwnerJpaRepository owners;
    @Autowired PatientJpaRepository patients;

    @Test
    void persistsOwnerAndPatient() {
        OwnerEntity o = new OwnerEntity();
        o.setName("John Doe");
        o.setEmail("john@vetflow.com");
        o.setPhone("+525512345678");
        o.setAddress("CDMX");

        OwnerEntity savedOwner = owners.save(o);
        assertThat(savedOwner.getId()).isNotNull();

        PatientEntity p = new PatientEntity();
        p.setName("Firulais");
        p.setSpecies("dog");
        p.setBreed("Mestizo");
        p.setBirthDate(LocalDate.of(2020, 1, 1));
        p.setWeight(new BigDecimal("12.30"));
        p.setOwner(savedOwner);

        PatientEntity savedPatient = patients.save(p);
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getOwner().getId()).isEqualTo(savedOwner.getId());
    }
}
