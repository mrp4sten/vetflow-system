package com.vetflow.api.application.medicalrecord;

import java.time.LocalDateTime;

/** DTO representing a medical record entry. */
public record MedicalRecordResult(Long id,
                                  Long patientId,
                                  Long veterinarianId,
                                  LocalDateTime visitDate,
                                  String diagnosis,
                                  String treatment,
                                  String medications,
                                  String notes,
                                  LocalDateTime createdAt) {}