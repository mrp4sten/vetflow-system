package com.vetflow.api.web.v1;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.config.OpenApiConfig;

import com.vetflow.api.application.owner.CreateOwnerCommand;
import com.vetflow.api.application.owner.OwnerApplicationService;
import com.vetflow.api.application.owner.OwnerResult;
import com.vetflow.api.application.owner.UpdateOwnerCommand;
import com.vetflow.api.web.v1.owner.CreateOwnerRequest;
import com.vetflow.api.web.v1.owner.OwnerResponse;
import com.vetflow.api.web.v1.owner.UpdateOwnerRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** REST endpoints for managing owners. */
@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Validated
@Tag(name = "Owners", description = "Owner registry and contact management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class OwnerController {

  private final OwnerApplicationService ownerApplicationService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT')")
  @Operation(summary = "Create owner", description = "Registers a new pet owner with contact information.")
  public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
    OwnerResult result = ownerApplicationService.createOwner(
        new CreateOwnerCommand(request.name(), request.phone(), request.email(), request.address()));
    return ResponseEntity.created(URI.create("/api/v1/owners/" + result.id())).body(toResponse(result));
  }

  @GetMapping("/{ownerId}")
  @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT','VETERINARIAN')")
  @Operation(summary = "Get owner", description = "Fetches owner details by identifier.")
  public OwnerResponse getOwner(@PathVariable Long ownerId) {
    return toResponse(ownerApplicationService.getById(ownerId));
  }

  @PutMapping("/{ownerId}")
  @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT')")
  @Operation(summary = "Update owner", description = "Updates phone, email or address for an owner.")
  public OwnerResponse updateOwner(@PathVariable Long ownerId, @Valid @RequestBody UpdateOwnerRequest request) {
    OwnerResult result = ownerApplicationService
        .updateOwner(new UpdateOwnerCommand(ownerId, request.phone(), request.email(), request.address()));
    return toResponse(result);
  }

  private static OwnerResponse toResponse(OwnerResult result) {
    return new OwnerResponse(result.id(),
        result.name(),
        result.phone(),
        result.email(),
        result.address(),
        result.createdAt(),
        result.updatedAt());
  }
}
