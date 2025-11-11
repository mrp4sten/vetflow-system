// src/test/java/com/vetflow/api/infrastructure/persistence/adapter/OwnerRepositoryAdapterTest.java
package com.vetflow.api.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.mapper.OwnerMapper;
import com.vetflow.api.infrastructure.persistence.repository.OwnerJpaRepository;

class OwnerRepositoryAdapterTest {

    OwnerJpaRepository jpa = mock(OwnerJpaRepository.class);
    OwnerMapper mapper = mock(OwnerMapper.class);

    OwnerRepositoryAdapter adapter = new OwnerRepositoryAdapter(jpa, mapper);

    @Test
    void savesAndReturnsDomain() {
        Owner domain = Owner.builder().id(1L).name("John").email("j@v.com").phone("1").address("A").build();
        OwnerEntity entity = new OwnerEntity();
        OwnerEntity savedEntity = new OwnerEntity();
        savedEntity.setId(1L);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(domain);

        Owner result = adapter.save(domain);

        assertThat(result).isEqualTo(domain);
        verify(jpa).save(entity);
    }

    @Test
    void findsByEmail() {
        OwnerEntity e = new OwnerEntity();
        e.setId(2L);
        when(jpa.findByEmail("x@v.com")).thenReturn(Optional.of(e));
        Owner domain = Owner.builder().id(2L).name("X").email("x@v.com").build();
        when(mapper.toDomain(e)).thenReturn(domain);

        Optional<Owner> res = adapter.findByEmail("x@v.com");

        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(2L);
    }
}
