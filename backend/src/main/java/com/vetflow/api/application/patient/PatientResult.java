package com.vetflow.api.application.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Lightweight DTO summarising patient state. */
public record PatientResult(Long id,
                            String name,
                            String species,
                            String breed,
                            LocalDate birthDate,
                            Long ownerId,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt) {}