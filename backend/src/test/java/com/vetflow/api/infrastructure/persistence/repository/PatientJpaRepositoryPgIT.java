package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vetflow.api.infrastructure.persistence.AbstractPostgresDataJpaTest;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

class PatientJpaRepositoryPgIT extends AbstractPostgresDataJpaTest {

    @Autowired OwnerJpaRepository owners;
    @Autowired PatientJpaRepository patients;

    private OwnerEntity newOwner() {
        OwnerEntity o = new OwnerEntity();
        o.setName("PG Owner");
        o.setEmail("pg-owner@vetflow.com");
        o.setPhone("+525500000000");
        o.setAddress("CDMX");
        return owners.saveAndFlush(o);
    }

    @Test
    @DisplayName("saves patient and finds by ownerId (Postgres)")
    void savesAndQueriesByOwner() {
        OwnerEntity owner = newOwner();

        PatientEntity p = new PatientEntity();
        p.setName("Firulais-pg");
        p.setSpecies("dog");
        p.setBreed("Mestizo");
        p.setBirthDate(LocalDate.of(2020, 1, 1));
        p.setWeight(new BigDecimal("10.50"));
        p.setOwner(owner);

        patients.saveAndFlush(p);

        assertThat(p.getId()).isNotNull();

        assertThat(patients.findByOwnerId(owner.getId()))
            .extracting(PatientEntity::getName)
            .contains("Firulais-pg");
    }
}
