package com.vetflow.api.web.v1.error;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vetflow.api.application.shared.ResourceNotFoundException;
import com.vetflow.api.application.shared.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/** Centralized exception handling for the API layer. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
        .toList();
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors, request.getRequestURI());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest request) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
        .map(this::toFieldError)
        .toList();
    return buildResponse(HttpStatus.BAD_REQUEST, "Constraint violation", fieldErrors, request.getRequestURI());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", null, request.getRequestURI());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request.getRequestURI());
  }

  @ExceptionHandler({ ValidationException.class, IllegalArgumentException.class })
  public ResponseEntity<ErrorResponse> handleValidation(RuntimeException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request.getRequestURI());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.CONFLICT, "Request conflicts with existing data", null, request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.FORBIDDEN, "Access is denied", null, request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneral(Exception ex, HttpServletRequest request) {
    ErrorResponse body = new ErrorResponse(Instant.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        "Unexpected error occurred",
        request.getRequestURI(),
        null);
    return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ErrorResponse.FieldError toFieldError(ConstraintViolation<?> violation) {
    String path = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
    return new ErrorResponse.FieldError(path, violation.getMessage());
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message,
      List<ErrorResponse.FieldError> fieldErrors, String path) {
    ErrorResponse body = new ErrorResponse(Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        path,
        fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors);
    return new ResponseEntity<>(body, status);
  }
}
