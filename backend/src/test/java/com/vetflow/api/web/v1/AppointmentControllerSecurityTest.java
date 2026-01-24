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
import com.vetflow.api.application.appointment.AppointmentApplicationService;
import com.vetflow.api.application.appointment.AppointmentResult;
import com.vetflow.api.domain.model.Appointment.Priority;
import com.vetflow.api.domain.model.Appointment.Status;
import com.vetflow.api.domain.model.Appointment.Type;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import({ AppointmentController.class, GlobalExceptionHandler.class, AppointmentControllerSecurityTest.TestSecurityConfig.class })
class AppointmentControllerSecurityTest {

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
  private AppointmentApplicationService appointmentApplicationService;

  @Test
  @WithMockUser(username = "assistant", roles = "ASSISTANT")
  void scheduleAppointmentAllowedForAssistantRole() throws Exception {
    AppointmentResult result = new AppointmentResult(1L,
        2L,
        null,
        LocalDateTime.parse("2024-01-01T10:00:00"),
        Type.CHECKUP,
        Status.SCHEDULED,
        Priority.NORMAL,
        "Routine check",
        LocalDateTime.now());
    when(appointmentApplicationService.scheduleAppointment(any())).thenReturn(result);

    String payload = objectMapper.writeValueAsString(new AppointmentRequestBody());

    mockMvc.perform(post("/api/v1/appointments")
        .contentType("application/json")
        .content(payload))
        .andExpect(status().isCreated());
  }

  private record AppointmentRequestBody(Long patientId, String appointmentDate, String type, String priority, String notes) {
    private AppointmentRequestBody() {
      this(2L, "2024-01-01T10:00:00", "CHECKUP", "NORMAL", "Routine check");
    }
  }
}
