package com.vetflow.api.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;

public interface SystemUserJpaRepository extends JpaRepository<SystemUserEntity, Long> {

  Optional<SystemUserEntity> findByUsernameIgnoreCase(String username);
}
