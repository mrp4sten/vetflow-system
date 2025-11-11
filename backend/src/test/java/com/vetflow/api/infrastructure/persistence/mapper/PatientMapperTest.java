// src/test/java/com/vetflow/api/infrastructure/persistence/mapper/PatientMapperTest.java
package com.vetflow.api.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.model.Patient.Species;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

class PatientMapperTest {

  private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

  @Test
  @DisplayName("Maps species to lowercase on entity")
  void speciesLowercase_toEntity() {
    Patient domain = Patient.builder()
        .id(1L)
        .name("Firulais")
        .breed("Mestizo")
        .species(Species.DOG)
        .birthDate(LocalDate.of(2020, 1, 1))
        .owner(Owner.builder().id(7L).name("Owner").email("o@v.com").phone("x").address("y").build())
        .build();

    PatientEntity entity = mapper.toEntity(domain);

    assertThat(entity.getSpecies()).isEqualTo("dog");
    assertThat(entity.getOwner()).isNotNull();
    assertThat(entity.getOwner().getId()).isEqualTo(7L);
  }

  @Test
  @DisplayName("Round-trip keeps species normalized")
  void roundTrip_species() {
    Patient domain = Patient.builder()
        .name("Luna")
        .species(Species.CAT)
        .owner(Owner.builder().id(9L).name("Ana").email("a@v.com").phone("p").address("addr").build())
        .build();

    PatientEntity entity = mapper.toEntity(domain);
    Patient back = mapper.toDomain(entity);

    assertThat(entity.getSpecies()).isEqualTo("cat");
    assertThat(back.getSpecies()).isEqualTo(Patient.Species.CAT);
  }
}
