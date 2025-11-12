package com.vetflow.api.web.v1.owner;

import java.time.LocalDateTime;

/** Response payload representing an owner. */
public record OwnerResponse(Long id,
                            String name,
                            String phone,
                            String email,
                            String address,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt) {
}