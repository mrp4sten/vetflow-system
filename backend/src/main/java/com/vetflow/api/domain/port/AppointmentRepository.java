package com.vetflow.api.domain.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.vetflow.api.domain.model.Appointment;

public interface AppointmentRepository {
    Appointment save(Appointment appt);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByPatient(Long patientId);
    List<Appointment> findByDateRange(LocalDateTime from, LocalDateTime to);
    void deleteById(Long id);
}
