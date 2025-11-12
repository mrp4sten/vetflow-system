package com.vetflow.api.application.shared;

/**
 * Signals invalid input supplied to an application service command.
 */
public class ValidationException extends ApplicationException {

  public ValidationException(String message) {
    super(message);
  }
}