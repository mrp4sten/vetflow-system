package com.vetflow.api.web.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.application.medicalrecord.MedicalRecordApplicationService;
import com.vetflow.api.application.medicalrecord.MedicalRecordResult;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import({ MedicalRecordController.class, GlobalExceptionHandler.class, MedicalRecordControllerSecurityTest.TestSecurityConfig.class })
class MedicalRecordControllerSecurityTest {

  @Configuration
  @EnableMethodSecurity
  static class TestSecurityConfig {
    @Bean
    SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(registry -> registry.anyRequest().authenticated());
      return http.build();
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private MedicalRecordApplicationService medicalRecordApplicationService;

  @Test
  @WithMockUser(username = "assistant", roles = "ASSISTANT")
  void createMedicalRecordForbiddenForAssistantRole() throws Exception {
    String payload = objectMapper.writeValueAsString(new MedicalRecordPayload());

    mockMvc.perform(post("/api/v1/medical-records")
        .contentType("application/json")
        .content(payload))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "vet", roles = "VETERINARIAN")
  void createMedicalRecordAllowedForVeterinarianRole() throws Exception {
    MedicalRecordResult result = new MedicalRecordResult(5L,
        2L,
        1L,
        LocalDateTime.parse("2024-01-01T09:00:00"),
        "Healthy",
        "Check",
        "None",
        "Notes",
        LocalDateTime.now());
    when(medicalRecordApplicationService.createMedicalRecord(any())).thenReturn(result);

    String payload = objectMapper.writeValueAsString(new MedicalRecordPayload());

    mockMvc.perform(post("/api/v1/medical-records")
        .contentType("application/json")
        .content(payload))
        .andExpect(status().isCreated());
  }

  private record MedicalRecordPayload(Long patientId, Long veterinarianId, String visitDate, String diagnosis, String treatment,
      String medications, String notes) {
    private MedicalRecordPayload() {
      this(2L, 1L, "2024-01-01T09:00:00", "Healthy", "Check", "None", "Notes");
    }
  }
}
