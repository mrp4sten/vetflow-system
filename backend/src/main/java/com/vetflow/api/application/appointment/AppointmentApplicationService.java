package com.vetflow.api.application.appointment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.AppointmentRepository;
import com.vetflow.api.domain.port.PatientRepository;

import lombok.RequiredArgsConstructor;

/** Application service orchestrating appointment-related use cases. */
@Service
@RequiredArgsConstructor
public class AppointmentApplicationService {

  private final AppointmentRepository appointmentRepository;
  private final PatientRepository patientRepository;
  private final AuditService auditService;
  private static final String COMMAND_MUST_NOT_BE_NULL_MESSAGE = "command must not be null";
  private static final String APPOINTMENT_TABLE_NAME = "appointments";

  public AppointmentResult scheduleAppointment(ScheduleAppointmentCommand command) {
    Objects.requireNonNull(command, COMMAND_MUST_NOT_BE_NULL_MESSAGE);
    Patient patient = loadPatient(command.patientId());
    
    // Prevent scheduling appointments for inactive patients
    if (!patient.isActive()) {
      throw new ValidationException("Cannot schedule appointments for inactive patients");
    }
    
    if (command.appointmentDate() == null) {
      throw new ValidationException("appointmentDate is required");
    }
    if (command.type() == null) {
      throw new ValidationException("appointment type is required");
    }

    Appointment appointment = Appointment.schedule(patient,
        command.veterinarianId(),
        command.appointmentDate(),
        command.type(),
        command.notes());
    if (command.priority() != null && command.priority() != appointment.getPriority()) {
      appointment.changePriority(command.priority());
    }
    Appointment saved = appointmentRepository.save(appointment);
    auditService.recordCreation(APPOINTMENT_TABLE_NAME, saved.getId(), saved);
    return toResult(saved);
  }

  public AppointmentResult rescheduleAppointment(RescheduleAppointmentCommand command) {
    Objects.requireNonNull(command, COMMAND_MUST_NOT_BE_NULL_MESSAGE);
    if (command.appointmentId() == null) {
      throw new ValidationException("appointmentId is required");
    }
    if (command.newDate() == null) {
      throw new ValidationException("newDate is required");
    }
    Appointment appointment = loadAppointment(command.appointmentId());
    Map<String, Object> before = auditService.snapshot(appointment);
    appointment.reschedule(command.newDate());
    Appointment saved = appointmentRepository.save(appointment);
    auditService.recordUpdate(APPOINTMENT_TABLE_NAME, saved.getId(), before, saved);
    return toResult(saved);
  }

  public AppointmentResult cancelAppointment(CancelAppointmentCommand command) {
    Objects.requireNonNull(command, COMMAND_MUST_NOT_BE_NULL_MESSAGE);
    if (command.appointmentId() == null) {
      throw new ValidationException("appointmentId is required");
    }
    Appointment appointment = loadAppointment(command.appointmentId());
    Map<String, Object> before = auditService.snapshot(appointment);
    appointment.cancel(command.reason());
    Appointment saved = appointmentRepository.save(appointment);
    auditService.recordUpdate(APPOINTMENT_TABLE_NAME, saved.getId(), before, saved);
    return toResult(saved);
  }

  public List<AppointmentResult> listAll() {
    return appointmentRepository.findAll().stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
        .map(this::toResult)
        .toList();
  }

  public List<AppointmentResult> listByPatient(Long patientId) {
    Patient patient = loadPatient(patientId);
    return appointmentRepository.findByPatient(patient.getId()).stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
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

  private Appointment loadAppointment(Long appointmentId) {
    return appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment %d not found".formatted(appointmentId)));
  }

  private AppointmentResult toResult(Appointment appointment) {
    return new AppointmentResult(appointment.getId(),
        appointment.getPatient().getId(),
        appointment.getVeterinarianId(),
        appointment.getAppointmentDate(),
        appointment.getType(),
        appointment.getStatus(),
        appointment.getPriority(),
        appointment.getNotes(),
        appointment.getCreatedAt());
  }
}
