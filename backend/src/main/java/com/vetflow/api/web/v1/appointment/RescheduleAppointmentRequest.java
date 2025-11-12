package com.vetflow.api.web.v1.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/** Request payload for rescheduling an appointment. */
public record RescheduleAppointmentRequest(
    @NotNull(message = "newDate is required")
    LocalDateTime newDate) {
}