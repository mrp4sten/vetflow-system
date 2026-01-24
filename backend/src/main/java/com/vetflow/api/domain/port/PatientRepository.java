package com.vetflow.api.domain.port;

import java.util.List;
import java.util.Optional;

import com.vetflow.api.domain.model.Patient;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(Long id);
    List<Patient> findAll();
    List<Patient> findByOwnerId(Long ownerId);
    List<Patient> findByActive(boolean active);
    List<Patient> findByOwnerIdAndActive(Long ownerId, boolean active);
    void deleteById(Long id);
}
