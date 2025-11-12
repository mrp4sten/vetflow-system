package com.vetflow.api.web.v1;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.application.patient.PatientApplicationService;
import com.vetflow.api.application.patient.PatientResult;
import com.vetflow.api.application.patient.RegisterPatientCommand;
import com.vetflow.api.application.patient.UpdatePatientCommand;
import com.vetflow.api.web.v1.patient.PatientResponse;
import com.vetflow.api.web.v1.patient.RegisterPatientRequest;
import com.vetflow.api.web.v1.patient.UpdatePatientRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** REST endpoints for managing patients. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class PatientController {

  private final PatientApplicationService patientApplicationService;

  @PostMapping("/patients")
  public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
    PatientResult result = patientApplicationService.registerPatient(
        new RegisterPatientCommand(request.name(),
            request.species(),
            request.breed(),
            request.birthDate(),
            request.ownerId()));
    return ResponseEntity.created(URI.create("/api/v1/patients/" + result.id())).body(toResponse(result));
  }

  @PutMapping("/patients/{patientId}")
  public PatientResponse updatePatient(@PathVariable Long patientId,
      @Valid @RequestBody UpdatePatientRequest request) {
    PatientResult result = patientApplicationService.updatePatient(
        new UpdatePatientCommand(patientId,
            request.name(),
            request.species(),
            request.breed(),
            request.birthDate(),
            request.ownerId()));
    return toResponse(result);
  }

  @GetMapping("/owners/{ownerId}/patients")
  public List<PatientResponse> listByOwner(@PathVariable Long ownerId) {
    return patientApplicationService.listByOwner(ownerId).stream()
        .map(PatientController::toResponse)
        .toList();
  }

  private static PatientResponse toResponse(PatientResult result) {
    return new PatientResponse(result.id(),
        result.name(),
        result.species(),
        result.breed(),
        result.birthDate(),
        result.ownerId(),
        result.createdAt(),
        result.updatedAt());
  }
}