package com.vetflow.api.web.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.vetflow.api.application.medicalrecord.CreateMedicalRecordCommand;
import com.vetflow.api.application.medicalrecord.MedicalRecordApplicationService;
import com.vetflow.api.application.medicalrecord.MedicalRecordResult;
import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;
import com.vetflow.api.web.v1.medicalrecord.CreateMedicalRecordRequest;

@WebMvcTest(controllers = MedicalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MedicalRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    MedicalRecordApplicationService medicalRecordApplicationService;

    @Test
    void createMedicalRecordReturnsCreated() throws Exception {
        LocalDateTime visitDate = LocalDateTime.now();
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest(1L, 2L, visitDate, "Diagnosis", "Treatment",
                "Medications", "Notes");
        MedicalRecordResult result = new MedicalRecordResult(5L, 1L, 2L, visitDate, "Diagnosis", "Treatment",
                "Medications",
                "Notes", LocalDateTime.now());
        given(medicalRecordApplicationService.createMedicalRecord(any(CreateMedicalRecordCommand.class)))
                .willReturn(result);

        mockMvc.perform(post("/api/v1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("/api/v1/medical-records/5")))
                .andExpect(jsonPath("$.diagnosis", is("Diagnosis")));
    }

    @Test
    void createMedicalRecordValidationError() throws Exception {
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest(null, -1L, null, "", "Treatment",
                "Medications",
                "Notes");

        mockMvc.perform(post("/api/v1/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listMedicalRecordsByPatientReturnsResults() throws Exception {
        LocalDateTime visitDate = LocalDateTime.now();
        List<MedicalRecordResult> results = List.of(
                new MedicalRecordResult(1L, 1L, 2L, visitDate, "Diagnosis", "Treatment", "Medications", "Notes",
                        LocalDateTime.now()),
                new MedicalRecordResult(2L, 1L, 2L, visitDate, "Diagnosis2", "Treatment2", "Medications2", "Notes2",
                        LocalDateTime.now()));
        given(medicalRecordApplicationService.listByPatient(1L)).willReturn(results);

        mockMvc.perform(get("/api/v1/patients/1/medical-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void listMedicalRecordsByPatientNotFound() throws Exception {
        given(medicalRecordApplicationService.listByPatient(1L))
                .willThrow(new ResourceNotFoundException("Patient 1 not found"));

        mockMvc.perform(get("/api/v1/patients/1/medical-records"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Patient 1 not found")));
    }
}
