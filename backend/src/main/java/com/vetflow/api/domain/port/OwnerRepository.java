package com.vetflow.api.domain.port;

import java.util.Optional;

import com.vetflow.api.domain.model.Owner;

public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(Long id);
    Optional<Owner> findByEmail(String email);
    void deleteById(Long id);
}
