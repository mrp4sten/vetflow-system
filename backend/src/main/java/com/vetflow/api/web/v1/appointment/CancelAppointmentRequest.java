package com.vetflow.api.web.v1.appointment;

import jakarta.validation.constraints.Size;

/** Request payload for canceling an appointment. */
public record CancelAppointmentRequest(
    @Size(max = 1000, message = "reason must be at most 1000 characters")
    String reason) {
}