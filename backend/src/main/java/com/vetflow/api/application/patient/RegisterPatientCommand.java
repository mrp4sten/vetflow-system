package com.vetflow.api.application.patient;

import java.time.LocalDate;

/** Command to register a new patient for an owner. */
public record RegisterPatientCommand(String name,
                                     String species,
                                     String breed,
                                     LocalDate birthDate,
                                     Long ownerId) {}