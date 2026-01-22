package com.vetflow.api.web.v1.patient;

import jakarta.validation.constraints.NotNull;

/** Request payload for updating patient active status. */
public record UpdatePatientStatusRequest(@NotNull Boolean isActive) {
}
