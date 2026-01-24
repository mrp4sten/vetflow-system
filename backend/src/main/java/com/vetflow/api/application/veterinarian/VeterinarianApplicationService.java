package com.vetflow.api.application.veterinarian;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.domain.model.Veterinarian;
import com.vetflow.api.domain.port.VeterinarianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for veterinarian operations.
 * 
 * <p>Coordinates veterinarian-related use cases.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VeterinarianApplicationService {

  private final VeterinarianRepository veterinarianRepository;

  /**
   * Retrieves all active veterinarians.
   * 
   * @return list of active veterinarians
   */
  @Transactional(readOnly = true)
  public List<VeterinarianResult> listActiveVeterinarians() {
    log.debug("Listing all active veterinarians");
    return veterinarianRepository.findAllActive().stream()
        .map(VeterinarianResult::from)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all veterinarians (active and inactive).
   * 
   * @param includeInactive whether to include inactive veterinarians
   * @return list of veterinarians
   */
  @Transactional(readOnly = true)
  public List<VeterinarianResult> listVeterinarians(boolean includeInactive) {
    log.debug("Listing veterinarians (includeInactive={})", includeInactive);
    
    if (includeInactive) {
      return veterinarianRepository.findAll().stream()
          .map(VeterinarianResult::from)
          .collect(Collectors.toList());
    } else {
      return listActiveVeterinarians();
    }
  }

  /**
   * Retrieves a veterinarian by ID.
   * 
   * @param id the veterinarian ID
   * @return the veterinarian details
   * @throws ResourceNotFoundException if veterinarian not found
   */
  @Transactional(readOnly = true)
  public VeterinarianResult getVeterinarianById(Long id) {
    log.debug("Getting veterinarian with id={}", id);
    
    Veterinarian vet = veterinarianRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with id: " + id));
    
    return VeterinarianResult.from(vet);
  }

  /**
   * Checks if a veterinarian exists and is active.
   * 
   * @param id the veterinarian ID
   * @return true if active veterinarian exists
   */
  @Transactional(readOnly = true)
  public boolean isActiveVeterinarian(Long id) {
    return veterinarianRepository.isActiveVeterinarian(id);
  }
}
