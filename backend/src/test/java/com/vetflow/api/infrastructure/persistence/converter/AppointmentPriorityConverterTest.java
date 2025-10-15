package com.vetflow.api.infrastructure.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Priority;

class AppointmentPriorityConverterTest {

  private final AppointmentPriorityConverter conv = new AppointmentPriorityConverter();

  @Test
  @DisplayName("enum -> lowercase")
  void toDatabase_lowercase() {
    assertThat(conv.convertToDatabaseColumn(Priority.LOW)).isEqualTo("low");
    assertThat(conv.convertToDatabaseColumn(Priority.CRITICAL)).isEqualTo("critical");
  }

  @Test
  @DisplayName("string case-insensitive -> enum")
  void toEntity_caseInsensitive() {
    assertThat(conv.convertToEntityAttribute("LoW")).isEqualTo(Priority.LOW);
    assertThat(conv.convertToEntityAttribute("CRITICAL")).isEqualTo(Priority.CRITICAL);
  }

  @Test
  @DisplayName("Null-safe both ways")
  void nullSafe() {
    assertThat(conv.convertToDatabaseColumn(null)).isNull();
    assertThat(conv.convertToEntityAttribute(null)).isNull();
  }

  @Test
  @DisplayName("Invalid value throws")
  void invalid_throws() {
    assertThatThrownBy(() -> conv.convertToEntityAttribute("wat"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Round-trip all values")
  void roundTrip_allValues() {
    for (Priority p : Priority.values()) {
      String db = conv.convertToDatabaseColumn(p);
      assertThat(conv.convertToEntityAttribute(db)).isEqualTo(p);
    }
  }
}
