package com.vetflow.api.application.patient;

/** Command to deactivate an existing patient (soft delete). */
public record DeactivatePatientCommand(Long patientId) {}
