// src/main/java/com/vetflow/api/infrastructure/persistence/adapter/OwnerRepositoryAdapter.java
package com.vetflow.api.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.port.OwnerRepository;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.mapper.OwnerMapper;
import com.vetflow.api.infrastructure.persistence.repository.OwnerJpaRepository;

@Component
@Transactional
public class OwnerRepositoryAdapter implements OwnerRepository {

    private final OwnerJpaRepository jpa;
    private final OwnerMapper mapper;

    public OwnerRepositoryAdapter(OwnerJpaRepository jpa, OwnerMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Owner save(Owner owner) {
        OwnerEntity entity = mapper.toEntity(owner);
        OwnerEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Owner> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        return jpa.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Owner> findByEmail(String email) {
        return jpa.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
