package com.vetflow.api.application.shared;

/**
 * Exception thrown when an expected domain aggregate cannot be located by the
 * application service.
 */
public class ResourceNotFoundException extends ApplicationException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}