package com.vetflow.api.application.owner;

/** Command object for registering a new owner. */
public record CreateOwnerCommand(String name, String phone, String email, String address) {}