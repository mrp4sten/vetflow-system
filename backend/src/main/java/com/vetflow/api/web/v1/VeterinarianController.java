package com.vetflow.api.web.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.application.veterinarian.VeterinarianApplicationService;
import com.vetflow.api.web.v1.veterinarian.VeterinarianResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for veterinarian operations.
 * 
 * <p>Provides endpoints for querying veterinarian information.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/veterinarians")
@RequiredArgsConstructor
@Tag(name = "Veterinarians", description = "Veterinarian management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class VeterinarianController {

  private final VeterinarianApplicationService veterinarianService;

  /**
   * Lists all veterinarians.
   * 
   * <p>By default, returns only active veterinarians.
   * Use {@code ?includeInactive=true} to include inactive veterinarians.
   * </p>
   *
   * @param includeInactive whether to include inactive veterinarians
   * @return list of veterinarians
   */
  @Operation(summary = "List all veterinarians", 
             description = "Returns all veterinarians. By default, only active veterinarians are returned.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token")
  })
  @GetMapping
  @PreAuthorize("hasAnyAuthority('admin', 'veterinarian', 'assistant')")
  public ResponseEntity<List<VeterinarianResponse>> listVeterinarians(
      @Parameter(description = "Include inactive veterinarians in results")
      @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
    
    log.info("GET /api/v1/veterinarians (includeInactive={})", includeInactive);
    
    List<VeterinarianResponse> vets = veterinarianService.listVeterinarians(includeInactive).stream()
        .map(VeterinarianResponse::from)
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(vets);
  }

  /**
   * Gets a veterinarian by ID.
   *
   * @param id the veterinarian ID
   * @return the veterinarian details
   */
  @Operation(summary = "Get veterinarian by ID", 
             description = "Returns detailed information about a specific veterinarian")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Veterinarian found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token"),
      @ApiResponse(responseCode = "404", description = "Veterinarian not found")
  })
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('admin', 'veterinarian', 'assistant')")
  public ResponseEntity<VeterinarianResponse> getVeterinarianById(
      @Parameter(description = "Veterinarian ID", required = true, example = "1")
      @PathVariable Long id) {
    
    log.info("GET /api/v1/veterinarians/{}", id);
    
    VeterinarianResponse vet = VeterinarianResponse.from(
        veterinarianService.getVeterinarianById(id)
    );
    
    return ResponseEntity.ok(vet);
  }
}
