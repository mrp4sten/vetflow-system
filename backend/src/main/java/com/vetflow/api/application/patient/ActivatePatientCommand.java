package com.vetflow.api.application.patient;

/** Command to activate a deactivated patient. */
public record ActivatePatientCommand(Long patientId) {}
