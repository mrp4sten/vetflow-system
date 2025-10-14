package com.vetflow.api.infrastructure.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PatientEntityPojoTest {

  @Test
  @DisplayName("PatientEntity getters/setters, relation to Owner and equals/hashCode")
  void pojo_patient_basic() {
    OwnerEntity owner = new OwnerEntity();
    owner.setId(10L);
    owner.setName("Owner");
    owner.setEmail("owner@vetflow.com");
    owner.setPhone("+525500000000");

    PatientEntity p = new PatientEntity();
    p.setId(100L);
    p.setName("Firulais");
    p.setSpecies("dog");
    p.setBreed("Mestizo");
    p.setBirthDate(LocalDate.of(2020,1,1));
    p.setWeight(new BigDecimal("12.30"));
    p.setOwner(owner);

    assertThat(p.getId()).isEqualTo(100);
    assertThat(p.getName()).isEqualTo("Firulais");
    assertThat(p.getSpecies()).isEqualTo("dog");
    assertThat(p.getBreed()).isEqualTo("Mestizo");
    assertThat(p.getBirthDate()).isEqualTo(LocalDate.of(2020,1,1));
    assertThat(p.getWeight()).isEqualByComparingTo("12.30");
    assertThat(p.getOwner()).isSameAs(owner);

    PatientEntity p2 = new PatientEntity();
    p2.setId(101L);
    assertThat(p).isNotEqualTo(p2);
  }
}
