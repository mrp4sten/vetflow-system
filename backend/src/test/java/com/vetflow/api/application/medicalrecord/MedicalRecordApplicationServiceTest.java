package com.vetflow.api.application.medicalrecord;

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
import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.MedicalRecordRepository;
import com.vetflow.api.domain.port.PatientRepository;

@ExtendWith(MockitoExtension.class)
class MedicalRecordApplicationServiceTest {

  @Mock
  private MedicalRecordRepository medicalRecordRepository;
  @Mock
  private PatientRepository patientRepository;

  @InjectMocks
  private MedicalRecordApplicationService service;

  private Patient patient;
  private MedicalRecord medicalRecord;

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
    medicalRecord = MedicalRecord.builder()
        .id(3L)
        .patient(patient)
        .veterinarianId(9L)
        .visitDate(LocalDateTime.now().minusDays(1))
        .diagnosis("Healthy")
        .treatment("Vitamins")
        .medications("Meds")
        .notes("Notes")
        .createdAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  @Test
  void createMedicalRecord_success() {
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    when(medicalRecordRepository.save(any())).thenAnswer(inv -> inv.<MedicalRecord>getArgument(0).toBuilder().id(10L).build());

    CreateMedicalRecordCommand command = new CreateMedicalRecordCommand(patient.getId(),
        8L,
        LocalDateTime.now(),
        "Checkup",
        "Treatment",
        "Medication",
        "Notes");

    MedicalRecordResult result = service.createMedicalRecord(command);

    assertThat(result.id()).isEqualTo(10L);
    assertThat(result.patientId()).isEqualTo(patient.getId());
  }

  @Test
  void createMedicalRecord_invalidVet_throws() {
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

    CreateMedicalRecordCommand command = new CreateMedicalRecordCommand(patient.getId(),
        0L,
        LocalDateTime.now(),
        "Checkup",
        null,
        null,
        null);

    assertThatThrownBy(() -> service.createMedicalRecord(command))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void listByPatient_requiresExistingPatient() {
    when(patientRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.listByPatient(1L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void listByPatient_ordersDescending() {
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    MedicalRecord older = medicalRecord.toBuilder()
        .id(4L)
        .visitDate(LocalDateTime.now().minusDays(3))
        .build();
    MedicalRecord newer = medicalRecord.toBuilder()
        .id(5L)
        .visitDate(LocalDateTime.now().minusHours(1))
        .build();
    when(medicalRecordRepository.findByPatientId(patient.getId())).thenReturn(List.of(older, newer));

    List<MedicalRecordResult> results = service.listByPatient(patient.getId());

    assertThat(results).hasSize(2);
    assertThat(results.get(0).id()).isEqualTo(newer.getId());
  }
}