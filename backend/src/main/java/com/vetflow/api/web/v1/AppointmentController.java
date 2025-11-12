package com.vetflow.api.web.v1;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.application.appointment.AppointmentApplicationService;
import com.vetflow.api.application.appointment.AppointmentResult;
import com.vetflow.api.application.appointment.CancelAppointmentCommand;
import com.vetflow.api.application.appointment.RescheduleAppointmentCommand;
import com.vetflow.api.application.appointment.ScheduleAppointmentCommand;
import com.vetflow.api.domain.model.Appointment.Priority;
import com.vetflow.api.domain.model.Appointment.Type;
import com.vetflow.api.web.v1.appointment.AppointmentResponse;
import com.vetflow.api.web.v1.appointment.CancelAppointmentRequest;
import com.vetflow.api.web.v1.appointment.RescheduleAppointmentRequest;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest.AppointmentPriority;
import com.vetflow.api.web.v1.appointment.ScheduleAppointmentRequest.AppointmentType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** REST endpoints for managing appointments. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

  private final AppointmentApplicationService appointmentApplicationService;

  @PostMapping("/appointments")
  public ResponseEntity<AppointmentResponse> scheduleAppointment(
      @Valid @RequestBody ScheduleAppointmentRequest request) {
    ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(
        request.patientId(),
        request.appointmentDate(),
        mapType(request.type()),
        mapPriority(request.priority()),
        request.notes());
    AppointmentResult result = appointmentApplicationService.scheduleAppointment(command);
    return ResponseEntity.created(URI.create("/api/v1/appointments/" + result.id())).body(toResponse(result));
  }

  @PatchMapping("/appointments/{appointmentId}/reschedule")
  public AppointmentResponse rescheduleAppointment(@PathVariable Long appointmentId,
      @Valid @RequestBody RescheduleAppointmentRequest request) {
    AppointmentResult result = appointmentApplicationService
        .rescheduleAppointment(new RescheduleAppointmentCommand(appointmentId, request.newDate()));
    return toResponse(result);
  }

  @PatchMapping("/appointments/{appointmentId}/cancel")
  public AppointmentResponse cancelAppointment(@PathVariable Long appointmentId,
      @Valid @RequestBody CancelAppointmentRequest request) {
    AppointmentResult result = appointmentApplicationService
        .cancelAppointment(new CancelAppointmentCommand(appointmentId, request.reason()));
    return toResponse(result);
  }

  @GetMapping("/patients/{patientId}/appointments")
  public List<AppointmentResponse> listByPatient(@PathVariable Long patientId) {
    return appointmentApplicationService.listByPatient(patientId).stream()
        .map(AppointmentController::toResponse)
        .toList();
  }

  private static Type mapType(AppointmentType type) {
    return type == null ? null : Type.valueOf(type.name());
  }

  private static Priority mapPriority(AppointmentPriority priority) {
    return priority == null ? null : Priority.valueOf(priority.name());
  }

  private static AppointmentResponse toResponse(AppointmentResult result) {
    return new AppointmentResponse(result.id(),
        result.patientId(),
        result.appointmentDate(),
        result.type().name(),
        result.status().name(),
        result.priority().name(),
        result.notes(),
        result.createdAt());
  }
}