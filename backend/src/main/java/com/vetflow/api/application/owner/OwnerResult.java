package com.vetflow.api.application.owner;

import java.time.LocalDateTime;

/** Lightweight DTO representing an owner for API/application use. */
public record OwnerResult(Long id,
                          String name,
                          String phone,
                          String email,
                          String address,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {}