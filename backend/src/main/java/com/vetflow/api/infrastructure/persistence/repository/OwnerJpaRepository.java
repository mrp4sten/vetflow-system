package com.vetflow.api.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;

public interface OwnerJpaRepository extends JpaRepository<OwnerEntity, Long> {
    Optional<OwnerEntity> findByEmail(String email);
}
