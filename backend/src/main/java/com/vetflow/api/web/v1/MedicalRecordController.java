package com.vetflow.api.web.v1;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.config.OpenApiConfig;

import com.vetflow.api.application.medicalrecord.CreateMedicalRecordCommand;
import com.vetflow.api.application.medicalrecord.MedicalRecordApplicationService;
import com.vetflow.api.application.medicalrecord.MedicalRecordResult;
import com.vetflow.api.web.v1.medicalrecord.CreateMedicalRecordRequest;
import com.vetflow.api.web.v1.medicalrecord.MedicalRecordResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** REST endpoints for medical records. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "Medical Records", description = "Capture and review clinical history")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class MedicalRecordController {

  private final MedicalRecordApplicationService medicalRecordApplicationService;

  @GetMapping("/medical-records")
  @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT','VETERINARIAN')")
  @Operation(summary = "List all medical records", description = "Returns all medical records in the system.")
  public List<MedicalRecordResponse> listAll() {
    return medicalRecordApplicationService.listAll().stream()
        .map(MedicalRecordController::toResponse)
        .toList();
  }

  @PostMapping("/medical-records")
  @PreAuthorize("hasAnyRole('ADMIN','VETERINARIAN')")
  @Operation(summary = "Create medical record", description = "Stores findings, treatment and notes for a visit.")
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
  @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT','VETERINARIAN')")
  @Operation(summary = "List medical records by patient", description = "Shows the timeline of records for a patient.")
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
