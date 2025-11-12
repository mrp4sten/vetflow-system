package com.vetflow.api.web.v1.medicalrecord;

import java.time.LocalDateTime;

/** Response payload representing a medical record. */
public record MedicalRecordResponse(Long id,
                                    Long patientId,
                                    Long veterinarianId,
                                    LocalDateTime visitDate,
                                    String diagnosis,
                                    String treatment,
                                    String medications,
                                    String notes,
                                    LocalDateTime createdAt) {
}