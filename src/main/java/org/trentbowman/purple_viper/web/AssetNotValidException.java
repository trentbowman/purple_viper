package org.trentbowman.purple_viper.web;

import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@SuppressWarnings("serial")
public class AssetNotValidException extends RuntimeException {
  static public AssetNotValidException fromBindingResult(BindingResult bindingResult) {
    String validationErrors = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
    return new AssetNotValidException(validationErrors);
  }

  public AssetNotValidException(String message) {
    super(message);
  }
}
