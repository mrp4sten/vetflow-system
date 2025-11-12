package com.vetflow.api.web.v1.patient;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request payload for registering a patient. */
public record RegisterPatientRequest(
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    String name,

    @NotBlank(message = "species is required")
    @Pattern(regexp = "(?i)dog|cat", message = "species must be DOG or CAT")
    String species,

    @Size(max = 50, message = "breed must be at most 50 characters")
    String breed,

    @NotNull(message = "birthDate is required")
    @PastOrPresent(message = "birthDate cannot be in the future")
    LocalDate birthDate,

    @NotNull(message = "ownerId is required")
    Long ownerId) {
}