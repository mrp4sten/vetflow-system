package com.vetflow.api.infrastructure.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Type;

class AppointmentTypeConverterTest {

  private final AppointmentTypeConverter conv = new AppointmentTypeConverter();

  @Test
  @DisplayName("convertToDatabaseColumn: enum -> lowercase string")
  void toDatabase_lowercase() {
    assertThat(conv.convertToDatabaseColumn(Type.CHECKUP)).isEqualTo("checkup");
    assertThat(conv.convertToDatabaseColumn(Type.VACCINATION)).isEqualTo("vaccination");
  }

  @Test
  @DisplayName("convertToEntityAttribute: string (any case) -> enum")
  void toEntity_caseInsensitive() {
    assertThat(conv.convertToEntityAttribute("ChEckUp")).isEqualTo(Type.CHECKUP);
    assertThat(conv.convertToEntityAttribute("vaccination")).isEqualTo(Type.VACCINATION);
  }

  @Test
  @DisplayName("Null-safe both ways")
  void nullSafe() {
    assertThat(conv.convertToDatabaseColumn(null)).isNull();
    assertThat(conv.convertToEntityAttribute(null)).isNull();
  }

  @Test
  @DisplayName("Invalid DB value throws IllegalArgumentException")
  void invalidValue_throws() {
    assertThatThrownBy(() -> conv.convertToEntityAttribute("unknown-type"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Round-trip for all enum values")
  void roundTrip_allValues() {
    for (Type t : Type.values()) {
      String db = conv.convertToDatabaseColumn(t);
      assertThat(conv.convertToEntityAttribute(db)).isEqualTo(t);
    }
  }
}
