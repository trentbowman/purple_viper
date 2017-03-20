package org.trentbowman.purple_viper.validator.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.trentbowman.purple_viper.validator.constraintvalidators.URIValidator;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = URIValidator.class)
@Documented
public @interface URI {
  String scheme() default "";
  
  String message() default "{org.trentbowman.purple_viper.validator.uri.message}";

  Class<?>[] groups() default { };
  
  Class<? extends Payload>[] payload() default { };
  
  @Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
  @Retention(RUNTIME)
  @Documented
  @interface List {
    URI[] value();
  }

}
