package com.vetflow.api.application.patient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.OwnerRepository;
import com.vetflow.api.domain.port.PatientRepository;

import lombok.RequiredArgsConstructor;

/** Application service for patient aggregate operations. */
@Service
@RequiredArgsConstructor
public class PatientApplicationService {

  private final PatientRepository patientRepository;
  private final OwnerRepository ownerRepository;
  private final AuditService auditService;

  public PatientResult registerPatient(RegisterPatientCommand command) {
    Objects.requireNonNull(command, "command must not be null");
    Owner owner = loadOwner(command.ownerId());
    Patient patient = Patient.create(command.name(),
        command.species(),
        command.breed(),
        command.birthDate(),
        owner);
    Patient saved = patientRepository.save(patient);
    auditService.recordCreation("patients", saved.getId(), saved);
    return toResult(saved);
  }

  public PatientResult updatePatient(UpdatePatientCommand command) {
    Objects.requireNonNull(command, "command must not be null");
    if (command.patientId() == null) {
      throw new ValidationException("patientId is required");
    }

    Patient patient = patientRepository.findById(command.patientId())
        .orElseThrow(() -> new ResourceNotFoundException("Patient %d not found".formatted(command.patientId())));

    Map<String, Object> before = auditService.snapshot(patient);

    if (command.name() != null) {
      patient.rename(command.name());
    }

    Owner owner = command.ownerId() != null ? loadOwner(command.ownerId()) : patient.getOwner();
    patient.updateProfile(command.species() != null ? command.species() : patient.getSpecies().name(),
        command.breed() != null ? command.breed() : patient.getBreed(),
        command.birthDate() != null ? command.birthDate() : patient.getBirthDate(),
        owner);

    Patient saved = patientRepository.save(patient);
    auditService.recordUpdate("patients", saved.getId(), before, saved);
    return toResult(saved);
  }

  public List<PatientResult> listByOwner(Long ownerId) {
    Owner owner = loadOwner(ownerId);
    return patientRepository.findByOwnerId(owner.getId()).stream()
        .map(this::toResult)
        .collect(Collectors.toList());
  }

  private Owner loadOwner(Long ownerId) {
    if (ownerId == null) {
      throw new ValidationException("ownerId is required");
    }
    return ownerRepository.findById(ownerId)
        .orElseThrow(() -> new ResourceNotFoundException("Owner %d not found".formatted(ownerId)));
  }

  private PatientResult toResult(Patient patient) {
    return new PatientResult(patient.getId(),
        patient.getName(),
        patient.getSpecies().name(),
        patient.getBreed(),
        patient.getBirthDate(),
        patient.getOwner().getId(),
        patient.getCreatedAt(),
        patient.getUpdatedAt());
  }
}