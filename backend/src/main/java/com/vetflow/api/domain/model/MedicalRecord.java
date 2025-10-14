package com.vetflow.api.domain.model;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a medical record entry for a patient's visit.
 *
 * Design notes:
 * - DDD-rich entity with invariants and behavior.
 * - Hexagonal: no persistence annotations.
 * - Parameter Object (CreateCommand) to avoid long parameter lists (Sonar S107).
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class MedicalRecord {

  private Long id;

  private Patient patient;
  private Long veterinarianId;

  private LocalDateTime visitDate;
  private String diagnosis;
  private String treatment;   // optional
  private String medications; // optional
  private String notes;       // optional

  private LocalDateTime createdAt;

  // ===== Parameter Object to satisfy Sonar S107 =====
  @Getter
  @Builder
  @AllArgsConstructor
  public static class CreateCommand {
    private final Patient patient;                 // required
    private final Long veterinarianId;            // required
    private final String diagnosis;               // required
    private final LocalDateTime visitDate;        // optional (null -> now)
    private final String treatment;               // optional
    private final String medications;             // optional
    private final String notes;                   // optional
  }

  // ========= FACTORIES (convenience) =========

  /** Convenience factory (no explicit visitDate, system clock). */
  public static MedicalRecord create(Patient patient,
                                     Long veterinarianId,
                                     String diagnosis,
                                     String treatment,
                                     String medications,
                                     String notes) {
    CreateCommand cmd = CreateCommand.builder()
        .patient(patient)
        .veterinarianId(veterinarianId)
        .diagnosis(diagnosis)
        .treatment(treatment)
        .medications(medications)
        .notes(notes)
        .build();
    return create(cmd, Clock.systemDefaultZone());
  }

  /** Convenience factory (explicit visitDate, system clock). */
  public static MedicalRecord create(Patient patient,
                                     Long veterinarianId,
                                     LocalDateTime visitDate,
                                     String diagnosis,
                                     String treatment,
                                     String medications,
                                     String notes) {
    CreateCommand cmd = CreateCommand.builder()
        .patient(patient)
        .veterinarianId(veterinarianId)
        .visitDate(visitDate)
        .diagnosis(diagnosis)
        .treatment(treatment)
        .medications(medications)
        .notes(notes)
        .build();
    return create(cmd, Clock.systemDefaultZone());
  }

  /** Primary factory: Command + Clock (ideal para tests). */
  static MedicalRecord create(CreateCommand cmd, Clock clock) {
    Objects.requireNonNull(cmd, "CreateCommand cannot be null");
    Objects.requireNonNull(cmd.getPatient(), "Medical record patient cannot be null");
    Objects.requireNonNull(cmd.getVeterinarianId(), "Medical record veterinarianId cannot be null");

    String dx = validateDiagnosis(cmd.getDiagnosis());
    LocalDateTime now = LocalDateTime.now(clock);

    return MedicalRecord.builder()
        .patient(cmd.getPatient())
        .veterinarianId(cmd.getVeterinarianId())
        .visitDate(cmd.getVisitDate() == null ? now : cmd.getVisitDate())
        .diagnosis(dx)
        .treatment(normalize(cmd.getTreatment()))
        .medications(normalize(cmd.getMedications()))
        .notes(normalize(cmd.getNotes()))
        .createdAt(now)
        .build();
  }

  // ========= DOMAIN BEHAVIOR =========

  public void updateDiagnosis(String newDiagnosis) {
    this.diagnosis = validateDiagnosis(newDiagnosis);
  }

  public void updateTreatment(String newTreatment) {
    this.treatment = normalize(newTreatment);
  }

  public void updateMedications(String newMedications) {
    this.medications = normalize(newMedications);
  }

  public void appendNotes(String extraNotes) {
    String n = normalize(extraNotes);
    if (n == null) return;
    if (this.notes == null || this.notes.isBlank()) this.notes = n;
    else this.notes = this.notes + "\n" + n;
  }

  void setId(Long id) { this.id = id; }

  // ========= VALIDATIONS =========

  private static String validateDiagnosis(String diagnosis) {
    if (diagnosis == null || diagnosis.trim().isEmpty())
      throw new IllegalArgumentException("Diagnosis cannot be null or empty");
    return diagnosis.trim();
  }

  private static String normalize(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
