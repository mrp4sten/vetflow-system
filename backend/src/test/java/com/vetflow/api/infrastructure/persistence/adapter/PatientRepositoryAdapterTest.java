package com.vetflow.api.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.domain.model.Patient.Species;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;
import com.vetflow.api.infrastructure.persistence.mapper.PatientMapper;
import com.vetflow.api.infrastructure.persistence.repository.PatientJpaRepository;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryAdapterTest {

    @Mock PatientJpaRepository jpa;
    @Mock PatientMapper mapper;

    @InjectMocks PatientRepositoryAdapter adapter;

    @Test
    @DisplayName("save() maps domain -> entity -> domain")
    void save_maps() {
        Patient domain = Patient.builder()
                .name("Luna")
                .owner(Owner.builder().id(7L).build())
                .species(Species.CAT)
                .build();

        PatientEntity entity = new PatientEntity();
        entity.setId(11L);
        entity.setName("Luna");
        entity.setOwner(new OwnerEntity());
        entity.getOwner().setId(7L);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(
                domain.toBuilder().id(11L).build()
        );

        Patient saved = adapter.save(domain);

        assertThat(saved.getId()).isEqualTo(11L);
        verify(mapper).toEntity(domain);
        verify(jpa).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findByOwnerId() maps list")
    void findByOwnerId_mapsList() {
        PatientEntity e1 = new PatientEntity();
        e1.setId(1L);
        e1.setName("Firu");

        when(jpa.findByOwnerId(9L)).thenReturn(List.of(e1));
        when(mapper.toDomain(e1)).thenReturn(
                Patient.builder().id(1L).name("Firu").build()
        );

        List<Patient> out = adapter.findByOwnerId(9L);

        assertThat(out).hasSize(1);
        assertThat(out.get(0).getName()).isEqualTo("Firu");
        verify(jpa).findByOwnerId(9L);
        verify(mapper).toDomain(e1);
    }

    @Test
    @DisplayName("findById() delegates to JPA")
    void findById() {
        PatientEntity e = new PatientEntity();
        e.setId(5L);
        when(jpa.findById(5L)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(Patient.builder().id(5L).build());

        Optional<Patient> out = adapter.findById(5L);

        assertThat(out).isPresent();
        verify(jpa).findById(5L);
        verify(mapper).toDomain(e);
    }
}
