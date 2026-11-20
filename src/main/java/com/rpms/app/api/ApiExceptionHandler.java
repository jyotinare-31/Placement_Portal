package com.rpms.app.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(EntityNotFoundException.class)
  ResponseEntity<Map<String, Object>> notFound(EntityNotFoundException exception) { return error(HttpStatus.NOT_FOUND, exception.getMessage()); }

  @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
  ResponseEntity<Map<String, Object>> invalid(RuntimeException exception) { return error(HttpStatus.BAD_REQUEST, exception.getMessage()); }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> invalidBody(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream().map(field -> field.getField() + ": " + field.getDefaultMessage()).distinct().collect(Collectors.joining("; "));
    return error(HttpStatus.BAD_REQUEST, message.isBlank() ? "Request validation failed." : message);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException exception) { return error(HttpStatus.CONFLICT, "The change conflicts with an existing RPMS record or database rule."); }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<Map<String, Object>> forbidden(AccessDeniedException exception) { return error(HttpStatus.FORBIDDEN, "Your role does not allow this action."); }

  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<Map<String, Object>> status(ResponseStatusException exception) { return error(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason() == null ? "Request failed." : exception.getReason()); }

  private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
    Map<String, Object> body = new LinkedHashMap<>(); body.put("message", message); body.put("timestamp", Instant.now());
    return ResponseEntity.status(status).body(body);
  }
}
