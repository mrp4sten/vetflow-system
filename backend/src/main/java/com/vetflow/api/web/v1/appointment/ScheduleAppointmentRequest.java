package com.vetflow.api.web.v1.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request payload for scheduling an appointment. */
public record ScheduleAppointmentRequest(
    @NotNull(message = "patientId is required")
    Long patientId,

    @NotNull(message = "appointmentDate is required")
    LocalDateTime appointmentDate,

    @NotNull(message = "type is required")
    AppointmentType type,

    AppointmentPriority priority,

    @Size(max = 1000, message = "notes must be at most 1000 characters")
    String notes) {

  public enum AppointmentType { CHECKUP, VACCINATION, SURGERY, GROOMING, EMERGENCY }

  public enum AppointmentPriority { LOW, NORMAL, HIGH, CRITICAL }
}