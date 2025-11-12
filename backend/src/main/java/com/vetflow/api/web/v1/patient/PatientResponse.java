package com.vetflow.api.web.v1.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response payload representing a patient. */
public record PatientResponse(Long id,
                              String name,
                              String species,
                              String breed,
                              LocalDate birthDate,
                              Long ownerId,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}