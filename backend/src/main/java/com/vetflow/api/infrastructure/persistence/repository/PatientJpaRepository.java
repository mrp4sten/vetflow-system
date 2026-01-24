package com.vetflow.api.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {
    List<PatientEntity> findByOwnerId(Long ownerId);
    List<PatientEntity> findByIsActive(boolean isActive);
    List<PatientEntity> findByOwnerIdAndIsActive(Long ownerId, boolean isActive);
}
