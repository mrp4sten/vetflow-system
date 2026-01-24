// src/main/java/com/vetflow/api/infrastructure/persistence/adapter/PatientRepositoryAdapter.java
package com.vetflow.api.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.port.PatientRepository;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;
import com.vetflow.api.infrastructure.persistence.mapper.PatientMapper;
import com.vetflow.api.infrastructure.persistence.repository.PatientJpaRepository;

@Component
@Transactional
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpa;
    private final PatientMapper mapper;

    public PatientRepositoryAdapter(PatientJpaRepository jpa, PatientMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = mapper.toEntity(patient);
        PatientEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return jpa.findAll()
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByOwnerId(Long ownerId) {
        return jpa.findByOwnerId(ownerId)
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByActive(boolean active) {
        return jpa.findByIsActive(active)
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByOwnerIdAndActive(Long ownerId, boolean active) {
        return jpa.findByOwnerIdAndIsActive(ownerId, active)
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
