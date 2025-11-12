package com.vetflow.api.application.appointment;

import java.time.LocalDateTime;

/** Command to reschedule an existing appointment. */
public record RescheduleAppointmentCommand(Long appointmentId, LocalDateTime newDate) {}