package com.vetflow.api.web.v1.patient;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request payload for updating a patient. */
public record UpdatePatientRequest(
    @Size(min = 1, max = 100, message = "name must be between 1 and 100 characters")
    String name,

    @Pattern(regexp = "(?i)dog|cat", message = "species must be DOG or CAT")
    String species,

    @Size(min = 1, max = 50, message = "breed must be between 1 and 50 characters")
    String breed,

    @PastOrPresent(message = "birthDate cannot be in the future")
    LocalDate birthDate,

    Long ownerId) {

  @AssertTrue(message = "At least one field must be provided to update a patient")
  public boolean hasUpdates() {
    return (name != null && !name.isBlank())
        || (species != null && !species.isBlank())
        || (breed != null && !breed.isBlank())
        || birthDate != null
        || ownerId != null;
  }
}