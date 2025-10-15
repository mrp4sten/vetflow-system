package com.vetflow.api.infrastructure.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status;

class AppointmentStatusConverterTest {

  private final AppointmentStatusConverter conv = new AppointmentStatusConverter();

  @Test
  @DisplayName("enum -> lowercase")
  void toDatabase_lowercase() {
    assertThat(conv.convertToDatabaseColumn(Status.SCHEDULED)).isEqualTo("scheduled");
    assertThat(conv.convertToDatabaseColumn(Status.NO_SHOW)).isEqualTo("no_show");
  }

  @Test
  @DisplayName("string case-insensitive -> enum")
  void toEntity_caseInsensitive() {
    assertThat(conv.convertToEntityAttribute("ScHeDuLeD")).isEqualTo(Status.SCHEDULED);
    assertThat(conv.convertToEntityAttribute("no_Show")).isEqualTo(Status.NO_SHOW);
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
    assertThatThrownBy(() -> conv.convertToEntityAttribute("foobar"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Round-trip all values")
  void roundTrip_allValues() {
    for (Status s : Status.values()) {
      String db = conv.convertToDatabaseColumn(s);
      assertThat(conv.convertToEntityAttribute(db)).isEqualTo(s);
    }
  }
}
