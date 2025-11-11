package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vetflow.api.infrastructure.persistence.AbstractPostgresDataJpaTest;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Priority;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Type;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

class AppointmentJpaRepositoryPgIT extends AbstractPostgresDataJpaTest {

    @Autowired OwnerJpaRepository owners;
    @Autowired PatientJpaRepository patients;
    @Autowired AppointmentJpaRepository appointments;

    private PatientEntity newPatient() {
        OwnerEntity owner = new OwnerEntity();
        owner.setName("Owner appt");
        owner.setEmail("owner-appt@vetflow.com");
        owner.setPhone("+525500000000");
        owner.setAddress("CDMX");
        OwnerEntity savedOwner = owners.saveAndFlush(owner);

        PatientEntity p = new PatientEntity();
        p.setName("Paciente appt");
        p.setSpecies("dog");
        p.setOwner(savedOwner);
        return patients.saveAndFlush(p);
    }

    @Test
    @DisplayName("saves appointment and queries by patient / status / date (Postgres)")
    void savesAndQueries() {
        PatientEntity patient = newPatient();

        AppointmentEntity a1 = new AppointmentEntity();
        a1.setPatient(patient);
        a1.setAppointmentDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        a1.setType(Type.CHECKUP);
        a1.setStatus(Status.SCHEDULED);
        a1.setPriority(Priority.NORMAL);
        appointments.saveAndFlush(a1);

        AppointmentEntity a2 = new AppointmentEntity();
        a2.setPatient(patient);
        a2.setAppointmentDate(LocalDateTime.of(2025, 1, 2, 10, 0));
        a2.setType(Type.VACCINATION);
        a2.setStatus(Status.COMPLETED);
        a2.setPriority(Priority.HIGH);
        appointments.saveAndFlush(a2);

        List<AppointmentEntity> byPatient =
            appointments.findByPatientId(patient.getId());
        assertThat(byPatient).hasSize(2);

        List<AppointmentEntity> byStatus =
            appointments.findByStatus(Status.SCHEDULED);
        assertThat(byStatus).extracting(AppointmentEntity::getType)
            .contains(Type.CHECKUP);

        List<AppointmentEntity> byDate =
            appointments.findByAppointmentDateBetween(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 3, 0, 0));
        assertThat(byDate).hasSize(2);
    }
}
