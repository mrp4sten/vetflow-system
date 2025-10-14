package com.vetflow.api.infrastructure.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OwnerEntityPojoTest {

  @Test
  @DisplayName("OwnerEntity getters/setters and equals/hashCode")
  void pojo_getters_setters_equals_hashCode() {
    OwnerEntity o1 = new OwnerEntity();
    o1.setId(1L);
    o1.setName("John Doe");
    o1.setEmail("john@vetflow.com");
    o1.setPhone("+525511112233");
    o1.setAddress("CDMX");

    // getters
    assertThat(o1.getId()).isEqualTo(1);
    assertThat(o1.getName()).isEqualTo("John Doe");
    assertThat(o1.getEmail()).isEqualTo("john@vetflow.com");
    assertThat(o1.getPhone()).isEqualTo("+525511112233");
    assertThat(o1.getAddress()).isEqualTo("CDMX");

    // equals/hashCode
    OwnerEntity o2 = new OwnerEntity();
    o2.setId(2L);
    assertThat(o1).isNotEqualTo(o2);
  }
}
