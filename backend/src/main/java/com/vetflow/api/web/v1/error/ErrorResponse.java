package com.vetflow.api.web.v1.error;

import java.time.Instant;
import java.util.List;

/** Standard error body returned by the API. */
public record ErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            List<FieldError> fieldErrors) {

  /** Details of a single field validation error. */
  public record FieldError(String field, String message) {
  }
}