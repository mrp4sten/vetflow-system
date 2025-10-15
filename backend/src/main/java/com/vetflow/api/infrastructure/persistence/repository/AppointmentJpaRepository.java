package com.vetflow.api.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long> {
  List<AppointmentEntity> findByPatientId(Long patientId);
  List<AppointmentEntity> findByAppointmentDateBetween(LocalDateTime from, LocalDateTime to);
  List<AppointmentEntity> findByStatus(Status status);
}
