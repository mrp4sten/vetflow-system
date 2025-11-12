package com.vetflow.api.application.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.port.OwnerRepository;

@ExtendWith(MockitoExtension.class)
class OwnerApplicationServiceTest {

  @Mock
  private OwnerRepository ownerRepository;

  @InjectMocks
  private OwnerApplicationService service;

  private Owner existingOwner;

  @BeforeEach
  void setUp() {
    existingOwner = Owner.builder()
        .id(42L)
        .name("Alice")
        .email("alice@example.com")
        .phone("+12345678901")
        .address("Main Street 1")
        .createdAt(LocalDateTime.now().minusDays(1))
        .updatedAt(LocalDateTime.now().minusHours(1))
        .build();
  }

  @Test
  void createOwner_success() {
    when(ownerRepository.save(any())).thenAnswer(invocation -> {
      Owner owner = invocation.getArgument(0);
      return owner.toBuilder().id(1L).build();
    });

    CreateOwnerCommand command = new CreateOwnerCommand("Bob", "+12345678901", "bob@example.com", "123 Road");

    OwnerResult result = service.createOwner(command);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Bob");
  }

  @Test
  void updateOwner_ownerNotFound_throws() {
    UpdateOwnerCommand command = new UpdateOwnerCommand(999L, null, null, null);
    when(ownerRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateOwner(command))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getById_null_throwsValidation() {
    assertThatThrownBy(() -> service.getById(null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void updateOwner_updatesProvidedFields() {
    when(ownerRepository.findById(existingOwner.getId())).thenReturn(Optional.of(existingOwner));
    when(ownerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    UpdateOwnerCommand command = new UpdateOwnerCommand(existingOwner.getId(), "+09876543210", "new@example.com", "New Addr");

    OwnerResult result = service.updateOwner(command);

    assertThat(result.phone()).isEqualTo("+09876543210");
    assertThat(result.email()).isEqualTo("new@example.com");
    assertThat(result.address()).isEqualTo("New Addr");
  }
}