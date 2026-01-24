package com.vetflow.api.web.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.application.patient.ActivatePatientCommand;
import com.vetflow.api.application.patient.DeactivatePatientCommand;
import com.vetflow.api.application.patient.PatientApplicationService;
import com.vetflow.api.application.patient.PatientResult;
import com.vetflow.api.application.patient.RegisterPatientCommand;
import com.vetflow.api.application.patient.UpdatePatientCommand;
import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;
import com.vetflow.api.web.v1.patient.RegisterPatientRequest;
import com.vetflow.api.web.v1.patient.UpdatePatientRequest;
import com.vetflow.api.web.v1.patient.UpdatePatientStatusRequest;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PatientControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  PatientApplicationService patientApplicationService;

  @Test
  void registerPatientReturnsCreated() throws Exception {
    RegisterPatientRequest request = new RegisterPatientRequest("Firulais", "DOG", "Beagle", LocalDate.now(), null, 1L);
    PatientResult result = new PatientResult(10L, "Firulais", "DOG", "Beagle", LocalDate.now(), null, true, 1L,
        LocalDateTime.now(), LocalDateTime.now());
    given(patientApplicationService.registerPatient(any(RegisterPatientCommand.class))).willReturn(result);

    mockMvc.perform(post("/api/v1/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", is("/api/v1/patients/10")))
        .andExpect(jsonPath("$.name", is("Firulais")));
  }

  @Test
  void registerPatientValidationError() throws Exception {
    RegisterPatientRequest request = new RegisterPatientRequest("", "", "Beagle", LocalDate.now(), null, null);

    mockMvc.perform(post("/api/v1/patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updatePatientReturnsOk() throws Exception {
    LocalDate birthDate = LocalDate.now();
    UpdatePatientRequest request = new UpdatePatientRequest("Firulais", "DOG", "Beagle", birthDate, null, 2L);
    PatientResult result = new PatientResult(10L, "Firulais", "DOG", "Beagle", birthDate, null, true, 2L,
        LocalDateTime.now(), LocalDateTime.now());
    given(patientApplicationService
        .updatePatient(new UpdatePatientCommand(10L, "Firulais", "DOG", "Beagle", birthDate, null, 2L)))
        .willReturn(result);

    mockMvc.perform(put("/api/v1/patients/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ownerId", is(2)));
  }

  @Test
  void listPatientsByOwnerReturnsList() throws Exception {
    List<PatientResult> results = List.of(
        new PatientResult(1L, "Firulais", "DOG", "Beagle", LocalDate.now(), null, true, 5L, LocalDateTime.now(),
            LocalDateTime.now()),
        new PatientResult(2L, "Mishi", "CAT", "Siames", LocalDate.now(), null, true, 5L, LocalDateTime.now(), LocalDateTime.now()));
    given(patientApplicationService.listByOwner(5L)).willReturn(results);

    mockMvc.perform(get("/api/v1/owners/5/patients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void listPatientsByOwnerNotFound() throws Exception {
    given(patientApplicationService.listByOwner(5L)).willThrow(new ResourceNotFoundException("Owner 5 not found"));

    mockMvc.perform(get("/api/v1/owners/5/patients"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("Owner 5 not found")));
  }

  @Test
  void deactivatePatientReturnsOk() throws Exception {
    UpdatePatientStatusRequest request = new UpdatePatientStatusRequest(false);
    PatientResult result = new PatientResult(10L, "Firulais", "DOG", "Beagle", LocalDate.now(), null, false, 1L,
        LocalDateTime.now(), LocalDateTime.now());
    given(patientApplicationService.deactivatePatient(new DeactivatePatientCommand(10L))).willReturn(result);

    mockMvc.perform(patch("/api/v1/patients/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isActive", is(false)));
  }

  @Test
  void activatePatientReturnsOk() throws Exception {
    UpdatePatientStatusRequest request = new UpdatePatientStatusRequest(true);
    PatientResult result = new PatientResult(10L, "Firulais", "DOG", "Beagle", LocalDate.now(), null, true, 1L,
        LocalDateTime.now(), LocalDateTime.now());
    given(patientApplicationService.activatePatient(new ActivatePatientCommand(10L))).willReturn(result);

    mockMvc.perform(patch("/api/v1/patients/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isActive", is(true)));
  }

  @Test
  void deactivatePatientAlreadyInactive() throws Exception {
    UpdatePatientStatusRequest request = new UpdatePatientStatusRequest(false);
    given(patientApplicationService.deactivatePatient(new DeactivatePatientCommand(10L)))
        .willThrow(new IllegalStateException("Patient is already deactivated"));

    mockMvc.perform(patch("/api/v1/patients/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("Patient is already deactivated")));
  }

  @Test
  void activatePatientAlreadyActive() throws Exception {
    UpdatePatientStatusRequest request = new UpdatePatientStatusRequest(true);
    given(patientApplicationService.activatePatient(new ActivatePatientCommand(10L)))
        .willThrow(new IllegalStateException("Patient is already active"));

    mockMvc.perform(patch("/api/v1/patients/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("Patient is already active")));
  }
}
