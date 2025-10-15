package com.vetflow.api.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;

public interface MedicalRecordJpaRepository extends JpaRepository<MedicalRecordEntity, Long> {
  List<MedicalRecordEntity> findByPatientIdOrderByVisitDateDesc(Long patientId);
}
