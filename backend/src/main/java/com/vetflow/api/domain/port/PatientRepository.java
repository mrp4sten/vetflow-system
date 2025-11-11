package com.vetflow.api.domain.port;

import java.util.List;
import java.util.Optional;

import com.vetflow.api.domain.model.Patient;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(Long id);
    List<Patient> findByOwnerId(Long ownerId);
    void deleteById(Long id);
}
