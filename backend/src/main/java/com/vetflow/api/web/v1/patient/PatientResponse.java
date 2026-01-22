package com.vetflow.api.web.v1.patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response payload representing a patient. */
public record PatientResponse(Long id,
                              String name,
                              String species,
                              String breed,
                              LocalDate birthDate,
                              BigDecimal weight,
                              Long ownerId,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}