package com.vetflow.api.application.medicalrecord;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.MedicalRecordRepository;
import com.vetflow.api.domain.port.PatientRepository;

import lombok.RequiredArgsConstructor;

/** Application service coordinating medical medicalRecord operations. */
@Service
@RequiredArgsConstructor
public class MedicalRecordApplicationService {

  private final MedicalRecordRepository medicalRecordRepository;
  private final PatientRepository patientRepository;
  private final AuditService auditService;

  public MedicalRecordResult createMedicalRecord(CreateMedicalRecordCommand command) {
    Objects.requireNonNull(command, "command must not be null");
    Patient patient = loadPatient(command.patientId());
    if (command.veterinarianId() == null || command.veterinarianId() <= 0) {
      throw new ValidationException("veterinarianId must be positive");
    }
    if (command.diagnosis() == null) {
      throw new ValidationException("diagnosis is required");
    }

    MedicalRecord medicalRecord = MedicalRecord.create(patient,
        command.veterinarianId(),
        command.visitDate(),
        command.diagnosis(),
        command.treatment(),
        command.medications(),
        command.notes());
    MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
    auditService.recordCreation("medical_records", saved.getId(), saved);
    return toResult(saved);
  }

  public List<MedicalRecordResult> listByPatient(Long patientId) {
    Patient patient = loadPatient(patientId);
    return medicalRecordRepository.findByPatientId(patient.getId()).stream()
        .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
        .map(this::toResult)
        .toList();
  }

  public List<MedicalRecordResult> listAll() {
    return medicalRecordRepository.findAll().stream()
        .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
        .map(this::toResult)
        .toList();
  }

  private Patient loadPatient(Long patientId) {
    if (patientId == null) {
      throw new ValidationException("patientId is required");
    }
    return patientRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient %d not found".formatted(patientId)));
  }

  private MedicalRecordResult toResult(MedicalRecord medicalRecord) {
    return new MedicalRecordResult(medicalRecord.getId(),
        medicalRecord.getPatient().getId(),
        medicalRecord.getVeterinarianId(),
        medicalRecord.getVisitDate(),
        medicalRecord.getDiagnosis(),
        medicalRecord.getTreatment(),
        medicalRecord.getMedications(),
        medicalRecord.getNotes(),
        medicalRecord.getCreatedAt());
  }
}