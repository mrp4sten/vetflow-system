package com.vetflow.api.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;

class OwnerMapperTest {

  private final OwnerMapper mapper = Mappers.getMapper(OwnerMapper.class);

  @Test
  @DisplayName("Owner round-trip domain <-> entity")
  void roundTrip() {
    Owner domain = Owner.builder()
        .id(10L)
        .name("John Doe")
        .email("john@vetflow.com")
        .phone("+525511112233")
        .address("CDMX")
        .build();

    OwnerEntity entity = mapper.toEntity(domain);
    assertThat(entity.getId()).isEqualTo(10L);
    assertThat(entity.getEmail()).isEqualTo("john@vetflow.com");

    Owner back = mapper.toDomain(entity);
    assertThat(back).usingRecursiveComparison().isEqualTo(domain);
  }
}
