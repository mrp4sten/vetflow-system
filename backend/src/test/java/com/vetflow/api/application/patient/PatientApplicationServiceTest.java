package com.vetflow.api.application.patient;

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
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.OwnerRepository;
import com.vetflow.api.domain.port.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientApplicationServiceTest {

  @Mock
  private PatientRepository patientRepository;
  @Mock
  private OwnerRepository ownerRepository;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private PatientApplicationService service;

  private Owner owner;
  private Patient patient;

  @BeforeEach
  void setUp() {
    owner = Owner.builder()
        .id(10L)
        .name("Alice")
        .email("alice@example.com")
        .phone("+12345678901")
        .address("123 Street")
        .createdAt(LocalDateTime.now().minusDays(10))
        .updatedAt(LocalDateTime.now().minusDays(1))
        .build();
    patient = Patient.builder()
        .id(55L)
        .name("Buddy")
        .species(Patient.Species.DOG)
        .breed("Labrador")
        .birthDate(LocalDate.now().minusYears(3))
        .owner(owner)
        .createdAt(LocalDateTime.now().minusDays(5))
        .updatedAt(LocalDateTime.now().minusDays(2))
        .build();
  }

  @Test
  void registerPatient_success() {
    when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
    when(patientRepository.save(any())).thenAnswer(inv -> inv.<Patient>getArgument(0).toBuilder().id(1L).build());

    RegisterPatientCommand command = new RegisterPatientCommand(
        "Buddy",
        "dog",
        "Labrador",
        LocalDate.now().minusYears(2),
        null,
        owner.getId());

    PatientResult result = service.registerPatient(command);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.ownerId()).isEqualTo(owner.getId());
  }

  @Test
  void updatePatient_notFound_throws() {
    when(patientRepository.findById(999L)).thenReturn(Optional.empty());

    UpdatePatientCommand command = new UpdatePatientCommand(999L, null, null, null, null, null, null);

    assertThatThrownBy(() -> service.updatePatient(command))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void listByOwner_ownerMustExist() {
    when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.listByOwner(1L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updatePatient_updatesProfile() {
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
    when(patientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    UpdatePatientCommand command = new UpdatePatientCommand(patient.getId(),
        "Rex",
        "dog",
        "Mixed",
        LocalDate.now().minusYears(4),
        null,
        owner.getId());

    PatientResult result = service.updatePatient(command);

    assertThat(result.name()).isEqualTo("Rex");
    assertThat(result.breed()).isEqualTo("Mixed");
  }

  @Test
  void listByOwner_returnsPatients() {
    when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
    when(patientRepository.findByOwnerId(owner.getId())).thenReturn(List.of(patient));

    List<PatientResult> results = service.listByOwner(owner.getId());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).id()).isEqualTo(patient.getId());
  }
}
