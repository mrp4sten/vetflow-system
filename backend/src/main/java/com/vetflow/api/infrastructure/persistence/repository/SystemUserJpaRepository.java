package com.vetflow.api.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;

public interface SystemUserJpaRepository extends JpaRepository<SystemUserEntity, Long> {

  Optional<SystemUserEntity> findByUsernameIgnoreCase(String username);

  // Veterinarian-specific queries
  
  @Query("SELECT u FROM SystemUserEntity u WHERE u.role = 'veterinarian'")
  List<SystemUserEntity> findAllVeterinarians();

  @Query("SELECT u FROM SystemUserEntity u WHERE u.role = 'veterinarian' AND u.active = true")
  List<SystemUserEntity> findAllActiveVeterinarians();

  @Query("SELECT u FROM SystemUserEntity u WHERE u.id = :id AND u.role = 'veterinarian'")
  Optional<SystemUserEntity> findVeterinarianById(@Param("id") Long id);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM SystemUserEntity u WHERE u.id = :id AND u.role = 'veterinarian' AND u.active = true")
  boolean isActiveVeterinarian(@Param("id") Long id);
}
