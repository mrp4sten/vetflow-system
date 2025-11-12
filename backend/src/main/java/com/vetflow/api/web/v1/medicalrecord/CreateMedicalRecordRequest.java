package com.vetflow.api.web.v1.medicalrecord;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Request payload for creating a medical record. */
public record CreateMedicalRecordRequest(
    @NotNull(message = "patientId is required")
    Long patientId,

    @NotNull(message = "veterinarianId is required")
    @Positive(message = "veterinarianId must be positive")
    Long veterinarianId,

    @NotNull(message = "visitDate is required")
    LocalDateTime visitDate,

    @NotBlank(message = "diagnosis is required")
    @Size(max = 500, message = "diagnosis must be at most 500 characters")
    String diagnosis,

    @Size(max = 500, message = "treatment must be at most 500 characters")
    String treatment,

    @Size(max = 500, message = "medications must be at most 500 characters")
    String medications,

    @Size(max = 1000, message = "notes must be at most 1000 characters")
    String notes) {
}