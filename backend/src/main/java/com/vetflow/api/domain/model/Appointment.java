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
 * Represents a veterinary appointment for a patient.
 *
 * <p><strong>Design notes</strong>:
 * <ul>
 *   <li>DDD-rich entity: encapsulates validation and state transitions.</li>
 *   <li>Hexagonal: no persistence annotations here.</li>
 *   <li>Controlled mutability: no public setters; use factory and domain methods.</li>
 * </ul>
 * </p>
 *
 * <p><strong>Invariants</strong>:
 * <ul>
 *   <li><code>patient</code>: required.</li>
 *   <li><code>veterinarianId</code>: optional, can be assigned later.</li>
 *   <li><code>appointmentDate</code>: required.</li>
 *   <li><code>type</code>: required, one of {@link Type}.</li>
 *   <li><code>status</code>: one of {@link Status}, defaults to SCHEDULED.</li>
 *   <li><code>priority</code>: one of {@link Priority}, defaults to NORMAL.</li>
 * </ul>
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Appointment {

  public enum Type { CHECKUP, VACCINATION, SURGERY, GROOMING, EMERGENCY }
  public enum Status { SCHEDULED, COMPLETED, CANCELLED, NO_SHOW }
  public enum Priority { LOW, NORMAL, HIGH, CRITICAL }

  private Long id;

  private Patient patient;
  private Long veterinarianId; // nullable - can be assigned later
  private LocalDateTime appointmentDate;
  private Type type;
  private Status status;
  private Priority priority;

  private String notes; // optional, business text
  private LocalDateTime createdAt;

  // ========= FACTORY METHODS =========

  /**
   * Creates a new Appointment with defaults (status=SCHEDULED, priority=NORMAL).
   */
  public static Appointment schedule(Patient patient,
                                     LocalDateTime appointmentDate,
                                     Type type,
                                     String notes) {
    return schedule(patient, null, appointmentDate, type, Priority.NORMAL, notes, Clock.systemDefaultZone());
  }

  /**
   * Creates a new Appointment with veterinarian assignment.
   */
  public static Appointment schedule(Patient patient,
                                     Long veterinarianId,
                                     LocalDateTime appointmentDate,
                                     Type type,
                                     String notes) {
    return schedule(patient, veterinarianId, appointmentDate, type, Priority.NORMAL, notes, Clock.systemDefaultZone());
  }

  /**
   * Creates a new Appointment with explicit priority and a test-friendly clock.
   */
  static Appointment schedule(Patient patient,
                              Long veterinarianId,
                              LocalDateTime appointmentDate,
                              Type type,
                              Priority priority,
                              String notes,
                              Clock clock) {
    Objects.requireNonNull(patient, "Appointment patient cannot be null");
    Objects.requireNonNull(appointmentDate, "Appointment date cannot be null");
    Objects.requireNonNull(type, "Appointment type cannot be null");
    Priority p = (priority == null) ? Priority.NORMAL : priority;

    LocalDateTime now = LocalDateTime.now(clock);
    return Appointment.builder()
        .patient(patient)
        .veterinarianId(veterinarianId)
        .appointmentDate(appointmentDate)
        .type(type)
        .status(Status.SCHEDULED)
        .priority(p)
        .notes(normalizeNotes(notes))
        .createdAt(now)
        .build();
  }

  // ========= DOMAIN BEHAVIOR =========

  /**
   * Reschedules the appointment. Allowed only while SCHEDULED.
   */
  public void reschedule(LocalDateTime newDate) {
    ensureStatus(Status.SCHEDULED, "Only scheduled appointments can be rescheduled");
    if (newDate == null) throw new IllegalArgumentException("New appointment date cannot be null");
    this.appointmentDate = newDate;
  }

  /**
   * Marks the appointment as completed. Allowed only while SCHEDULED.
   */
  public void complete() {
    ensureStatus(Status.SCHEDULED, "Only scheduled appointments can be completed");
    this.status = Status.COMPLETED;
  }

  /**
   * Cancels the appointment. Allowed only while SCHEDULED.
   * Optionally appends a reason to notes.
   */
  public void cancel(String reason) {
    ensureStatus(Status.SCHEDULED, "Only scheduled appointments can be cancelled");
    this.status = Status.CANCELLED;
    appendNotes(reason);
  }

  /**
   * Marks the appointment as no-show. Allowed only while SCHEDULED.
   */
  public void markNoShow(String note) {
    ensureStatus(Status.SCHEDULED, "Only scheduled appointments can be marked as no-show");
    this.status = Status.NO_SHOW;
    appendNotes(note);
  }

  /**
   * Changes the priority at any time (business rule can be tightened if needed).
   */
  public void changePriority(Priority newPriority) {
    if (newPriority == null) throw new IllegalArgumentException("Priority cannot be null");
    this.priority = newPriority;
  }

  /**
   * Assigns a veterinarian to this appointment. Allowed only while SCHEDULED.
   */
  public void assignVeterinarian(Long veterinarianId) {
    ensureStatus(Status.SCHEDULED, "Can only assign veterinarian to scheduled appointments");
    if (veterinarianId == null) throw new IllegalArgumentException("Veterinarian ID cannot be null");
    this.veterinarianId = veterinarianId;
  }

  /**
   * Removes the veterinarian assignment. Allowed only while SCHEDULED.
   */
  public void unassignVeterinarian() {
    ensureStatus(Status.SCHEDULED, "Can only unassign veterinarian from scheduled appointments");
    this.veterinarianId = null;
  }

  /**
   * Appends a line to the notes (ignores null/blank).
   */
  public void appendNotes(String extraNotes) {
    String n = normalizeNotes(extraNotes);
    if (n == null) return;
    if (this.notes == null || this.notes.isBlank()) {
      this.notes = n;
    } else {
      this.notes = this.notes + "\n" + n;
    }
  }

  void setId(Long id) { this.id = id; }

  private static String normalizeNotes(String text) {
    if (text == null) return null;
    String t = text.trim();
    return t.isEmpty() ? null : t;
  }

  private void ensureStatus(Status expected, String message) {
    if (this.status != expected) throw new IllegalStateException(message);
  }
}
