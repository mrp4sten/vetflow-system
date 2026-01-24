package com.vetflow.api.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vetflow.api.domain.model.Veterinarian;
import com.vetflow.api.domain.port.VeterinarianRepository;
import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;
import com.vetflow.api.infrastructure.persistence.repository.SystemUserJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Infrastructure adapter for Veterinarian repository.
 * 
 * <p>Maps between domain {@link Veterinarian} and JPA {@link SystemUserEntity} for users with role 'veterinarian'.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class VeterinarianRepositoryAdapter implements VeterinarianRepository {

  private final SystemUserJpaRepository systemUserJpaRepository;

  @Override
  public Optional<Veterinarian> findById(Long id) {
    return systemUserJpaRepository.findVeterinarianById(id)
        .map(this::mapToDomain);
  }

  @Override
  public List<Veterinarian> findAllActive() {
    return systemUserJpaRepository.findAllActiveVeterinarians().stream()
        .map(this::mapToDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Veterinarian> findAll() {
    return systemUserJpaRepository.findAllVeterinarians().stream()
        .map(this::mapToDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsById(Long id) {
    return systemUserJpaRepository.findVeterinarianById(id).isPresent();
  }

  @Override
  public boolean isActiveVeterinarian(Long id) {
    return systemUserJpaRepository.isActiveVeterinarian(id);
  }

  // ========= MAPPING =========

  private Veterinarian mapToDomain(SystemUserEntity entity) {
    return Veterinarian.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .email(entity.getEmail())
        .isActive(entity.isActive())
        .createdAt(entity.getCreatedAt())
        .lastLogin(entity.getLastLogin())
        .build();
  }
}
