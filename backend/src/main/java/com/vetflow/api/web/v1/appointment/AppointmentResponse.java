package com.vetflow.api.web.v1.appointment;

import java.time.LocalDateTime;

/** Response payload representing an appointment. */
public record AppointmentResponse(Long id,
                                  Long patientId,
                                  Long veterinarianId,
                                  LocalDateTime appointmentDate,
                                  String type,
                                  String status,
                                  String priority,
                                  String notes,
                                  LocalDateTime createdAt) {
}