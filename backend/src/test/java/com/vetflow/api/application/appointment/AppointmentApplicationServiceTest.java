package com.vetflow.api.application.appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.AppointmentRepository;
import com.vetflow.api.domain.port.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentApplicationServiceTest {

  @Mock
  private AppointmentRepository appointmentRepository;
  @Mock
  private PatientRepository patientRepository;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private AppointmentApplicationService service;

  private Patient patient;
  private Appointment scheduledAppointment;

  @BeforeEach
  void setUp() {
    Owner owner = Owner.builder()
        .id(1L)
        .name("Alice")
        .email("alice@example.com")
        .phone("+12345678901")
        .address("123 Street")
        .createdAt(LocalDateTime.now().minusDays(5))
        .updatedAt(LocalDateTime.now().minusDays(4))
        .build();
    patient = Patient.builder()
        .id(2L)
        .name("Buddy")
        .species(Patient.Species.DOG)
        .breed("Labrador")
        .birthDate(LocalDate.now().minusYears(3))
        .owner(owner)
        .createdAt(LocalDateTime.now().minusDays(3))
        .updatedAt(LocalDateTime.now().minusDays(2))
        .build();
    scheduledAppointment = Appointment.schedule(patient,
        LocalDateTime.now().plusDays(1),
        Appointment.Type.CHECKUP,
        "Initial visit").toBuilder().id(5L).build();
  }

  @Test
  void scheduleAppointment_success() {
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    when(appointmentRepository.save(any())).thenAnswer(inv -> inv.<Appointment>getArgument(0).toBuilder().id(10L).build());

    ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(patient.getId(),
        LocalDateTime.now().plusDays(2),
        Appointment.Type.SURGERY,
        Appointment.Priority.HIGH,
        "Surgery notes");

    AppointmentResult result = service.scheduleAppointment(command);

    assertThat(result.id()).isEqualTo(10L);
    assertThat(result.priority()).isEqualTo(Appointment.Priority.HIGH);
  }

  @Test
  void rescheduleAppointment_notFound() {
    when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

    RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(999L, LocalDateTime.now().plusDays(3));

    assertThatThrownBy(() -> service.rescheduleAppointment(command))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void rescheduleAppointment_requiresDate() {
    RescheduleAppointmentCommand rescheduleAppointmentCommand = new RescheduleAppointmentCommand(1L, null);
    assertThatThrownBy(() -> service.rescheduleAppointment(rescheduleAppointmentCommand))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void cancelAppointment_updatesStatus() {
    when(appointmentRepository.findById(scheduledAppointment.getId())).thenReturn(Optional.of(scheduledAppointment));
    when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AppointmentResult result = service.cancelAppointment(new CancelAppointmentCommand(scheduledAppointment.getId(), "Busy"));

    assertThat(result.status()).isEqualTo(Appointment.Status.CANCELLED);
  }

  @Test
  void listByPatient_sortsDescending() {
    Appointment older = scheduledAppointment.toBuilder()
        .id(6L)
        .appointmentDate(LocalDateTime.now().plusDays(1))
        .build();
    Appointment newer = scheduledAppointment.toBuilder()
        .id(7L)
        .appointmentDate(LocalDateTime.now().plusDays(5))
        .build();

    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    when(appointmentRepository.findByPatient(patient.getId())).thenReturn(List.of(older, newer));

    List<AppointmentResult> results = service.listByPatient(patient.getId());

    assertThat(results).hasSize(2);
    assertThat(results.get(0).id()).isEqualTo(newer.getId());
  }
}
