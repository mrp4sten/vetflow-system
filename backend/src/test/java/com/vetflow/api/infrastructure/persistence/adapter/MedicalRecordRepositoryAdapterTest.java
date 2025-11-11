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

import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;
import com.vetflow.api.infrastructure.persistence.mapper.MedicalRecordMapper;
import com.vetflow.api.infrastructure.persistence.repository.MedicalRecordJpaRepository;

@ExtendWith(MockitoExtension.class)
class MedicalRecordRepositoryAdapterTest {

    @Mock MedicalRecordJpaRepository jpa;
    @Mock MedicalRecordMapper mapper;

    @InjectMocks MedicalRecordRepositoryAdapter adapter;

    @Test
    @DisplayName("save() maps and delegates")
    void save_maps() {
        MedicalRecord domain = MedicalRecord.builder()
                .patient(Patient.builder().id(3L).build())
                .diagnosis("otitis")
                .visitDate(LocalDateTime.now())
                .veterinarianId(5L)
                .build();

        MedicalRecordEntity entity = new MedicalRecordEntity();
        entity.setId(99L);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(
                domain.toBuilder().id(99L).build()
        );

        MedicalRecord saved = adapter.save(domain);

        assertThat(saved.getId()).isEqualTo(99L);
        verify(mapper).toEntity(domain);
        verify(jpa).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() maps when present")
    void findById_maps() {
        MedicalRecordEntity e = new MedicalRecordEntity();
        e.setId(3L);

        when(jpa.findById(3L)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(MedicalRecord.builder().id(3L).build());

        Optional<MedicalRecord> out = adapter.findById(3L);

        assertThat(out).isPresent();
        verify(jpa).findById(3L);
        verify(mapper).toDomain(e);
    }
}
