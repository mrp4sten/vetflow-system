package com.vetflow.api.application.owner;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;
import com.vetflow.api.audit.AuditService;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.port.OwnerRepository;

import lombok.RequiredArgsConstructor;

/**
 * Application service orchestrating owner aggregate operations.
 */
@Service
@RequiredArgsConstructor
public class OwnerApplicationService {

  private final OwnerRepository ownerRepository;
  private final AuditService auditService;

  public OwnerResult createOwner(CreateOwnerCommand command) {
    Objects.requireNonNull(command, "command must not be null");
    Owner owner = Owner.create(command.name(), command.phone(), command.email(), command.address());
    Owner saved = ownerRepository.save(owner);
    auditService.recordCreation("owners", saved.getId(), saved);
    return toResult(saved);
  }

  public OwnerResult updateOwner(UpdateOwnerCommand command) {
    Objects.requireNonNull(command, "command must not be null");
    if (command.ownerId() == null) {
      throw new ValidationException("ownerId is required");
    }

    Owner owner = ownerRepository.findById(command.ownerId())
        .orElseThrow(() -> new ResourceNotFoundException("Owner %d not found".formatted(command.ownerId())));

    Map<String, Object> before = auditService.snapshot(owner);

    if (command.email() != null) {
      owner.changeEmail(command.email());
    }
    if (command.phone() != null) {
      owner.changePhone(command.phone());
    }
    if (command.address() != null) {
      owner.updateAddress(command.address());
    }

    Owner saved = ownerRepository.save(owner);
    auditService.recordUpdate("owners", saved.getId(), before, saved);
    return toResult(saved);
  }

  public OwnerResult getById(Long ownerId) {
    if (ownerId == null) {
      throw new ValidationException("ownerId is required");
    }
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new ResourceNotFoundException("Owner %d not found".formatted(ownerId)));
    return toResult(owner);
  }

  private OwnerResult toResult(Owner owner) {
    return new OwnerResult(owner.getId(),
        owner.getName(),
        owner.getPhone(),
        owner.getEmail(),
        owner.getAddress(),
        owner.getCreatedAt(),
        owner.getUpdatedAt());
  }
}