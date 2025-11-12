package com.vetflow.api.application.owner;

/** Command object for updating contact information for an existing owner. */
public record UpdateOwnerCommand(Long ownerId, String phone, String email, String address) {}