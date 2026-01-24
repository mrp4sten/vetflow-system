package com.vetflow.api.domain.port;

import java.util.List;
import java.util.Optional;

import com.vetflow.api.domain.model.Veterinarian;

/**
 * Repository port for veterinarian operations.
 * 
 * <p>This interface defines the contract for accessing veterinarian data
 * from the system_users table where role = 'veterinarian'.
 * </p>
 * 
 * <p>Implementation: {@link com.vetflow.api.infrastructure.persistence.adapter.VeterinarianRepositoryAdapter}
 * </p>
 */
public interface VeterinarianRepository {

  /**
   * Finds a veterinarian by ID.
   * 
   * @param id the system user ID
   * @return the veterinarian if found and has role 'veterinarian'
   */
  Optional<Veterinarian> findById(Long id);

  /**
   * Finds all active veterinarians.
   * 
   * @return list of active veterinarians
   */
  List<Veterinarian> findAllActive();

  /**
   * Finds all veterinarians (active and inactive).
   * 
   * @return list of all veterinarians
   */
  List<Veterinarian> findAll();

  /**
   * Checks if a veterinarian exists by ID.
   * 
   * @param id the system user ID
   * @return true if a veterinarian with this ID exists
   */
  boolean existsById(Long id);

  /**
   * Checks if a system user is a veterinarian and is active.
   * 
   * @param id the system user ID
   * @return true if user exists, has role 'veterinarian', and is active
   */
  boolean isActiveVeterinarian(Long id);
}
