package com.vetflow.api.application.appointment;

/** Command to cancel an existing appointment. */
public record CancelAppointmentCommand(Long appointmentId, String reason) {}