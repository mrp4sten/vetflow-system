package com.vetflow.api.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.domain.model.Appointment.Priority;
import com.vetflow.api.domain.model.Appointment.Status;
import com.vetflow.api.domain.model.Appointment.Type;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;

class AppointmentMapperTest {

    private final PatientMapperImpl patientMapper = new PatientMapperImpl();

    private AppointmentMapperImpl newMapper() {
        AppointmentMapperImpl m = new AppointmentMapperImpl();
        try {
            Field f = AppointmentMapperImpl.class.getDeclaredField("patientMapper");
            f.setAccessible(true);
            f.set(m, patientMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return m;
    }

    @Test
    @DisplayName("Domain -> Entity -> Domain keeps enums")
    void roundTrip_appointment() {
        AppointmentMapper mapper = newMapper();

        Patient patient = Patient.builder()
            .id(1L)
            .name("Firulais")
            .owner(Owner.builder().id(10L).name("John").email("j@v.com").phone("123").address("addr").build())
            .build();

        Appointment domain = Appointment.schedule(
            patient,
            LocalDateTime.of(2025, 1, 10, 10, 0),
            Type.CHECKUP,
            "first visit"
        );
        domain.changePriority(Priority.HIGH);

        // domain -> entity
        AppointmentEntity entity = mapper.toEntity(domain);

        // entity -> domain
        Appointment back = mapper.toDomain(entity);

        // entity enums
        assertThat(entity.getType())
            .isEqualTo(com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Type.CHECKUP);
        assertThat(entity.getStatus())
            .isEqualTo(com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status.SCHEDULED);
        assertThat(entity.getPriority())
            .isEqualTo(com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Priority.HIGH);

        // back to domain
        assertThat(back.getType()).isEqualTo(Type.CHECKUP);
        assertThat(back.getStatus()).isEqualTo(Status.SCHEDULED);
        assertThat(back.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(back.getPatient().getId()).isEqualTo(1L);
    }
}
