package org.trentbowman.purple_viper.validator.constraintvalidators;

import java.net.URISyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.trentbowman.purple_viper.validator.constraints.URI;

public class URIValidator implements ConstraintValidator<URI, CharSequence> {

  private String scheme;

  @Override
  public void initialize(URI annotation) {
    this.scheme = annotation.scheme();
  }

  @Override
  public boolean isValid(CharSequence value, ConstraintValidatorContext validatorContext) {
    if (value == null|| value.toString().trim().isEmpty()) {
      return true;
    }

    java.net.URI uri;
    try {
      uri = new java.net.URI(value.toString());
    } catch (URISyntaxException e) {
      uri = null;
    }
    
    if (uri == null) {
      return false;
    }
    
    if (!scheme.isEmpty()) {
      if (!scheme.equals(uri.getScheme())) {
        return false;
      }
    }

    return true;
  }

}
