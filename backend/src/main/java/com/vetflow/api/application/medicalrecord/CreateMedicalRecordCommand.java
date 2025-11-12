package com.vetflow.api.application.medicalrecord;

import java.time.LocalDateTime;

/** Command to create a new medical record entry for a patient visit. */
public record CreateMedicalRecordCommand(Long patientId,
                                         Long veterinarianId,
                                         LocalDateTime visitDate,
                                         String diagnosis,
                                         String treatment,
                                         String medications,
                                         String notes) {}
