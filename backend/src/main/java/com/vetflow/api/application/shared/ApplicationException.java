package com.vetflow.api.application.shared;

/**
 * Base type for application-layer specific exceptions. These represent
 * validation or orchestration errors detected while coordinating domain
 * operations.
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}