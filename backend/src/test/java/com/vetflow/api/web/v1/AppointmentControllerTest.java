package com.vetflow.api.web.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.vetflow.api.application.appointment.AppointmentApplicationService;
import com.vetflow.api.application.appointment.AppointmentResult;
import com.vetflow.api.application.appointment.CancelAppointmentCommand;
import com.vetflow.api.application.appointment.RescheduleAppointmentCommand;
import com.vetflow.api.application.appointment.ScheduleAppointmentCommand;
import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.web.v1.appointment.CancelAppointmentRequest;
import com.vetflow.api.web.v1.appointment.RescheduleAppointmentRequest;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest.AppointmentPriority;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest.AppointmentType;
import com.vetflow.api.web.v1.error.GlobalExceptionHandler;

@WebMvcTest(controllers = AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    AppointmentApplicationService appointmentApplicationService;

    @Test
    void scheduleAppointmentReturnsCreated() throws Exception {
        ScheduleAppointmentRequest request = new ScheduleAppointmentRequest(1L, LocalDateTime.now(),
                AppointmentType.CHECKUP,
                AppointmentPriority.HIGH, "Notes");
        AppointmentResult result = new AppointmentResult(5L, 1L, LocalDateTime.now(),
                com.vetflow.api.domain.model.Appointment.Type.CHECKUP,
                com.vetflow.api.domain.model.Appointment.Status.SCHEDULED,
                com.vetflow.api.domain.model.Appointment.Priority.HIGH,
                "Notes", LocalDateTime.now());
        given(appointmentApplicationService.scheduleAppointment(any(ScheduleAppointmentCommand.class)))
                .willReturn(result);

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("/api/v1/appointments/5")))
                .andExpect(jsonPath("$.type", is("CHECKUP")));
    }

    @Test
    void scheduleAppointmentValidationError() throws Exception {
        ScheduleAppointmentRequest request = new ScheduleAppointmentRequest(null, null, null, null, "Notes");

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rescheduleAppointmentReturnsOk() throws Exception {
        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest(LocalDateTime.now().plusDays(1));
        AppointmentResult result = new AppointmentResult(7L, 2L, LocalDateTime.now(),
                com.vetflow.api.domain.model.Appointment.Type.CHECKUP,
                com.vetflow.api.domain.model.Appointment.Status.SCHEDULED,
                com.vetflow.api.domain.model.Appointment.Priority.NORMAL,
                null, LocalDateTime.now());
        given(appointmentApplicationService
                .rescheduleAppointment(new RescheduleAppointmentCommand(7L, request.newDate())))
                .willReturn(result);

        mockMvc.perform(patch("/api/v1/appointments/7/reschedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId", is(2)));
    }

    @Test
    void cancelAppointmentReturnsOk() throws Exception {
        CancelAppointmentRequest request = new CancelAppointmentRequest("reason");
        AppointmentResult result = new AppointmentResult(8L, 3L, LocalDateTime.now(),
                com.vetflow.api.domain.model.Appointment.Type.CHECKUP,
                com.vetflow.api.domain.model.Appointment.Status.CANCELLED,
                com.vetflow.api.domain.model.Appointment.Priority.NORMAL,
                "reason", LocalDateTime.now());
        given(appointmentApplicationService.cancelAppointment(new CancelAppointmentCommand(8L, "reason")))
                .willReturn(result);

        mockMvc.perform(patch("/api/v1/appointments/8/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void listAppointmentsByPatientReturnsResults() throws Exception {
        List<AppointmentResult> results = List.of(
                new AppointmentResult(1L, 1L, LocalDateTime.now(),
                        com.vetflow.api.domain.model.Appointment.Type.CHECKUP,
                        com.vetflow.api.domain.model.Appointment.Status.SCHEDULED,
                        com.vetflow.api.domain.model.Appointment.Priority.NORMAL,
                        null, LocalDateTime.now()),
                new AppointmentResult(2L, 1L, LocalDateTime.now(),
                        com.vetflow.api.domain.model.Appointment.Type.SURGERY,
                        com.vetflow.api.domain.model.Appointment.Status.SCHEDULED,
                        com.vetflow.api.domain.model.Appointment.Priority.HIGH,
                        null, LocalDateTime.now()));
        given(appointmentApplicationService.listByPatient(1L)).willReturn(results);

        mockMvc.perform(get("/api/v1/patients/1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void listAppointmentsByPatientNotFound() throws Exception {
        given(appointmentApplicationService.listByPatient(1L))
                .willThrow(new ResourceNotFoundException("Patient 1 not found"));

        mockMvc.perform(get("/api/v1/patients/1/appointments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Patient 1 not found")));
    }
}
