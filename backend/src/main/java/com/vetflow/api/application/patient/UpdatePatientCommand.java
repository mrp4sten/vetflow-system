package com.vetflow.api.application.patient;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Command to update an existing patient profile. */
public record UpdatePatientCommand(Long patientId,
                                   String name,
                                   String species,
                                   String breed,
                                   LocalDate birthDate,
                                   BigDecimal weight,
                                   Long ownerId) {}