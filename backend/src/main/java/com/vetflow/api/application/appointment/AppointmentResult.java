package com.vetflow.api.application.appointment;

import java.time.LocalDateTime;

import com.vetflow.api.domain.model.Appointment.Priority;
import com.vetflow.api.domain.model.Appointment.Status;
import com.vetflow.api.domain.model.Appointment.Type;

/** DTO summarising appointment state. */
public record AppointmentResult(Long id,
                                Long patientId,
                                LocalDateTime appointmentDate,
                                Type type,
                                Status status,
                                Priority priority,
                                String notes,
                                LocalDateTime createdAt) {}