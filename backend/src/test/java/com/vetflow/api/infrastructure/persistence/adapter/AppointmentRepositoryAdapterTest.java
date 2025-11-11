package com.vetflow.api.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;
import com.vetflow.api.infrastructure.persistence.mapper.AppointmentMapper;
import com.vetflow.api.infrastructure.persistence.repository.AppointmentJpaRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentRepositoryAdapterTest {

    @Mock AppointmentJpaRepository jpa;
    @Mock AppointmentMapper mapper;

    @InjectMocks AppointmentRepositoryAdapter adapter;

    @Test
    @DisplayName("save() maps domain -> entity -> domain")
    void save_maps() {
        Appointment domain = Appointment.schedule(
                Patient.builder().id(3L).build(),
                LocalDateTime.of(2025, 1, 1, 10, 0),
                Appointment.Type.CHECKUP,
                "notes"
        );

        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(44L);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(
                domain.toBuilder().id(44L).build()
        );

        Appointment saved = adapter.save(domain);

        assertThat(saved.getId()).isEqualTo(44L);
        verify(mapper).toEntity(domain);
        verify(jpa).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() returns empty when missing")
    void findById_empty() {
        when(jpa.findById(99L)).thenReturn(Optional.empty());

        Optional<Appointment> out = adapter.findById(99L);

        assertThat(out).isEmpty();
    }
}
