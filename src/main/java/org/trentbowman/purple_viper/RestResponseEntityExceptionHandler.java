package org.trentbowman.purple_viper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.trentbowman.purple_viper.web.AssetNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { AssetNotFoundException.class })
  protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
    // TODO: possible JSON-injection in exception message
    String bodyOfResponse = "{\"error\":\"NOT_FOUND\", \"message\": \"" + ex.getMessage() + "\"";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return handleExceptionInternal(ex, bodyOfResponse, httpHeaders, HttpStatus.NOT_FOUND, request);
  }
}