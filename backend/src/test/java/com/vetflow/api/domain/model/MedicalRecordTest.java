package com.vetflow.api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MedicalRecord}.
 */
class MedicalRecordTest {

  private static final Instant FIXED = Instant.parse("2024-06-20T14:30:00Z");
  private static final Clock FIXED_CLOCK = Clock.fixed(FIXED, ZoneOffset.UTC);

  private Patient samplePatient() {
    Owner owner = Owner.create("John Doe", "+52 55 1234 5678", "john@e.com", "Addr");
    return Patient.create("Fido", "dog", "labrador",
        LocalDate.of(2020, 1, 1), owner, FIXED_CLOCK);
  }

  @Test
  @DisplayName("Should create medical record with defaults and normalized fields")
  void shouldCreateMedicalRecord() {
    Patient p = samplePatient();

    MedicalRecord mr = MedicalRecord.create(
        p,
        1001L,
        " Otitis externa ",
        "  Clean ear canal  ",
        "Amoxicillin 250mg",
        " follow-up in 7 days "
    );

    assertThat(mr.getId()).isNull();
    assertThat(mr.getPatient()).isEqualTo(p);
    assertThat(mr.getVeterinarianId()).isEqualTo(1001L);
    assertThat(mr.getDiagnosis()).isEqualTo("Otitis externa");
    assertThat(mr.getTreatment()).isEqualTo("Clean ear canal");
    assertThat(mr.getMedications()).isEqualTo("Amoxicillin 250mg");
    assertThat(mr.getNotes()).isEqualTo("follow-up in 7 days");
  }

  @Test
  @DisplayName("Should use explicit visitDate when provided")
  void shouldUseExplicitVisitDate() {
    Patient p = samplePatient();
    LocalDateTime visit = LocalDateTime.of(2024, 6, 1, 9, 30);

    MedicalRecord.CreateCommand cmd = MedicalRecord.CreateCommand.builder()
        .patient(p)
        .veterinarianId(2002L)
        .visitDate(visit)            // 👈 explícito
        .diagnosis("Dermatitis")     // 👈 requerido
        .build();

    MedicalRecord mr = MedicalRecord.create(cmd, FIXED_CLOCK);

    assertThat(mr.getVisitDate()).isEqualTo(visit);
  }

  @Test
  @DisplayName("Should throw when patient or veterinarianId is null")
  void shouldThrowOnNullPatientOrVet() {
    Patient p = samplePatient();

    assertThatThrownBy(() -> MedicalRecord.create(null, 1L, "Dx", null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Medical record patient cannot be null");

    assertThatThrownBy(() -> MedicalRecord.create(p, null, "Dx", null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Medical record veterinarianId cannot be null");
  }

  @Test
  @DisplayName("Should throw when diagnosis is null or empty")
  void shouldThrowOnInvalidDiagnosis() {
    Patient p = samplePatient();

    assertThatThrownBy(() -> MedicalRecord.create(p, 1L, null, null, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Diagnosis cannot be null or empty");

    assertThatThrownBy(() -> MedicalRecord.create(p, 1L, "   ", null, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Diagnosis cannot be null or empty");
  }

  @Test
  @DisplayName("Should update diagnosis/treatment/medications and append notes")
  void shouldUpdateClinicalFields() {
    Patient p = samplePatient();
    MedicalRecord mr = MedicalRecord.create(p, 1L, "Otitis", null, null, null);

    mr.updateDiagnosis("Chronic otitis");
    mr.updateTreatment("Topical steroids");
    mr.updateMedications("Prednisone 10mg");
    mr.appendNotes("owner informed");
    mr.appendNotes("  "); // ignored
    mr.appendNotes(null); // ignored

    assertThat(mr.getDiagnosis()).isEqualTo("Chronic otitis");
    assertThat(mr.getTreatment()).isEqualTo("Topical steroids");
    assertThat(mr.getMedications()).isEqualTo("Prednisone 10mg");
    assertThat(mr.getNotes()).isEqualTo("owner informed");
  }
}
