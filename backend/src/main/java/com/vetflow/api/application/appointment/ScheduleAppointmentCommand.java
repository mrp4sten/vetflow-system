package com.vetflow.api.application.appointment;

import java.time.LocalDateTime;

import com.vetflow.api.domain.model.Appointment.Priority;
import com.vetflow.api.domain.model.Appointment.Type;

/** Command to schedule a new appointment for a patient. */
public record ScheduleAppointmentCommand(Long patientId,
                                         LocalDateTime appointmentDate,
                                         Type type,
                                         Priority priority,
                                         String notes) {}
