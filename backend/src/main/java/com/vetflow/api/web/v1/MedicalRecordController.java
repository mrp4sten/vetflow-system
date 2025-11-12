package com.vetflow.api.web.v1;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.application.medicalrecord.CreateMedicalRecordCommand;
import com.vetflow.api.application.medicalrecord.MedicalRecordApplicationService;
import com.vetflow.api.application.medicalrecord.MedicalRecordResult;
import com.vetflow.api.web.v1.medicalrecord.CreateMedicalRecordRequest;
import com.vetflow.api.web.v1.medicalrecord.MedicalRecordResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** REST endpoints for medical records. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class MedicalRecordController {

  private final MedicalRecordApplicationService medicalRecordApplicationService;

  @PostMapping("/medical-records")
  public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
      @Valid @RequestBody CreateMedicalRecordRequest request) {
    MedicalRecordResult result = medicalRecordApplicationService.createMedicalRecord(
        new CreateMedicalRecordCommand(request.patientId(),
            request.veterinarianId(),
            request.visitDate(),
            request.diagnosis(),
            request.treatment(),
            request.medications(),
            request.notes()));
    return ResponseEntity.created(URI.create("/api/v1/medical-records/" + result.id())).body(toResponse(result));
  }

  @GetMapping("/patients/{patientId}/medical-records")
  public List<MedicalRecordResponse> listByPatient(@PathVariable Long patientId) {
    return medicalRecordApplicationService.listByPatient(patientId).stream()
        .map(MedicalRecordController::toResponse)
        .toList();
  }

  private static MedicalRecordResponse toResponse(MedicalRecordResult result) {
    return new MedicalRecordResponse(result.id(),
        result.patientId(),
        result.veterinarianId(),
        result.visitDate(),
        result.diagnosis(),
        result.treatment(),
        result.medications(),
        result.notes(),
        result.createdAt());
  }
}