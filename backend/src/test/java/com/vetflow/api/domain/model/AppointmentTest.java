package com.vetflow.api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Appointment}.
 */
class AppointmentTest {

  private static final Instant FIXED = Instant.parse("2024-06-15T09:00:00Z");
  private static final Clock FIXED_CLOCK = Clock.fixed(FIXED, ZoneOffset.UTC);

  private Patient samplePatient() {
    Owner owner = Owner.create("John Doe", "+52 55 1234 5678", "john@e.com", "Addr");
    return Patient.create("Fido", "dog", "labrador",
        java.time.LocalDate.of(2020, 1, 1), owner, FIXED_CLOCK);
  }

  @Test
  @DisplayName("Should schedule appointment with defaults (status=SCHEDULED, priority=NORMAL)")
  void shouldScheduleWithDefaults() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);

    Appointment appt = Appointment.schedule(p, when, Appointment.Type.CHECKUP, "first visit");

    assertThat(appt.getId()).isNull();
    assertThat(appt.getPatient()).isEqualTo(p);
    assertThat(appt.getAppointmentDate()).isEqualTo(when);
    assertThat(appt.getType()).isEqualTo(Appointment.Type.CHECKUP);
    assertThat(appt.getStatus()).isEqualTo(Appointment.Status.SCHEDULED);
    assertThat(appt.getPriority()).isEqualTo(Appointment.Priority.NORMAL);
    assertThat(appt.getNotes()).isEqualTo("first visit");
    assertThat(appt.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should schedule with explicit priority")
  void shouldScheduleWithExplicitPriority() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);

    Appointment appt = Appointment.schedule(
        p, when, Appointment.Type.EMERGENCY, Appointment.Priority.CRITICAL, "ER case", FIXED_CLOCK);

    assertThat(appt.getPriority()).isEqualTo(Appointment.Priority.CRITICAL);
    assertThat(appt.getCreatedAt()).isEqualTo(LocalDateTime.ofInstant(FIXED, ZoneOffset.UTC));
  }

  @Test
  @DisplayName("Should fail to schedule when required fields are null")
  void shouldFailOnNulls() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);

    assertThatThrownBy(() -> Appointment.schedule(null, when, Appointment.Type.CHECKUP, "x"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Appointment patient cannot be null");

    assertThatThrownBy(() -> Appointment.schedule(p, null, Appointment.Type.CHECKUP, "x"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Appointment date cannot be null");

    assertThatThrownBy(() -> Appointment.schedule(p, when, null, "x"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Appointment type cannot be null");
  }

  @Test
  @DisplayName("Should reschedule only when SCHEDULED")
  void shouldRescheduleOnlyWhenScheduled() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);
    Appointment appt = Appointment.schedule(p, when, Appointment.Type.CHECKUP, "x");

    LocalDateTime newWhen = LocalDateTime.of(2024, 7, 2, 11, 0);
    appt.reschedule(newWhen);
    assertThat(appt.getAppointmentDate()).isEqualTo(newWhen);

    appt.complete();

    // 👇 PRECALCULA FUERA DE LA LAMBDA
    LocalDateTime later = newWhen.plusDays(1);

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> appt.reschedule(later))
        .withMessageContaining("Only scheduled appointments can be rescheduled");
  }

  @Test
  @DisplayName("Should complete/cancel/no-show only when SCHEDULED")
  void shouldEnforceStateTransitions() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);
    Appointment appt = Appointment.schedule(p, when, Appointment.Type.VACCINATION, null);

    appt.complete();
    assertThat(appt.getStatus()).isEqualTo(Appointment.Status.COMPLETED);

    assertThatThrownBy(() -> appt.cancel("no longer needed"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only scheduled appointments can be cancelled");
  }

  @Test
  @DisplayName("Should change priority anytime")
  void shouldChangePriority() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);
    Appointment appt = Appointment.schedule(p, when, Appointment.Type.GROOMING, "note");

    appt.changePriority(Appointment.Priority.HIGH);
    assertThat(appt.getPriority()).isEqualTo(Appointment.Priority.HIGH);

    assertThatThrownBy(() -> appt.changePriority(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Priority cannot be null");
  }

  @Test
  @DisplayName("Should append notes gracefully")
  void shouldAppendNotes() {
    Patient p = samplePatient();
    LocalDateTime when = LocalDateTime.of(2024, 7, 1, 10, 0);
    Appointment appt = Appointment.schedule(p, when, Appointment.Type.CHECKUP, "A");

    appt.appendNotes("B");
    appt.appendNotes("  "); // ignored
    appt.appendNotes(null); // ignored

    assertThat(appt.getNotes()).isEqualTo("A\nB");
  }
}
