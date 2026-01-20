// src/main/java/com/vetflow/api/infrastructure/persistence/adapter/AppointmentRepositoryAdapter.java
package com.vetflow.api.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.domain.port.AppointmentRepository;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;
import com.vetflow.api.infrastructure.persistence.mapper.AppointmentMapper;
import com.vetflow.api.infrastructure.persistence.repository.AppointmentJpaRepository;

@Component
@Transactional
public class AppointmentRepositoryAdapter implements AppointmentRepository {

  private final AppointmentJpaRepository jpa;
  private final AppointmentMapper mapper;

  public AppointmentRepositoryAdapter(AppointmentJpaRepository jpa, AppointmentMapper mapper) {
    this.jpa = jpa;
    this.mapper = mapper;
  }

  @Override
  public Appointment save(Appointment appt) {
    AppointmentEntity entity = mapper.toEntity(appt);
    AppointmentEntity saved = jpa.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Appointment> findById(Long id) {
    return jpa.findById(id).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Appointment> findAll() {
    return jpa.findAll()
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Appointment> findByDateRange(LocalDateTime from, LocalDateTime to) {
    return jpa.findByAppointmentDateBetween(from, to)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Appointment> findByPatient(Long patientId) {
    return jpa.findByPatientId(patientId)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpa.deleteById(id);
  }
}
