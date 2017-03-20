package org.trentbowman.purple_viper.validator.constraintvalidators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.trentbowman.purple_viper.validator.constraints.URI;

public class URIValidatorTest {
  
    @URI
    public String nullString = null;
   
    @URI
    public String emptyString = "";

    @URI
    public String whitespaceString = "       ";

    // Example URL and URN from Wikipedia article: https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    
    @URI
    public String urlString = "abc://username:password@example.com:123/path/data?key=value&key2=value2#fragid1";

    @URI
    public String urnString = "urn:example:mammal:monotreme:echidna";

    @URI(scheme="git")
    public String uriWithMatchedScheme = "git://github.com/user/project-name.git";

    @URI(scheme="svn+ssl")
    public String uriWithMismatchedScheme = "git://github.com/user/project-name.git";

    private final URIValidator validator = new URIValidator();
    
    private final ConstraintValidatorContext validatorContext = Mockito.mock(ConstraintValidatorContext.class);
    
    private void initializeValidator(String fieldName) {
      try {
        validator.initialize(this.getClass().getField(fieldName).getAnnotation(URI.class));
      } catch (NoSuchFieldException e) {
        throw new IllegalArgumentException("No field named \"" + fieldName + "\"", e);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }
    
    @Test
    public void nullStringIsValid() {
      initializeValidator("nullString");
      
      assertTrue("null string be considered valid", validator.isValid(nullString, validatorContext));
    }

    @Test
    public void emptyStringIsValid() {
      initializeValidator("emptyString");
     
      assertTrue("Empty stringshould be considered valid", validator.isValid(emptyString, validatorContext));
    }

    @Test
    public void blankStringIsValid() {
      initializeValidator("whitespaceString");     
      
      assertTrue("Whitespace-only string should be considered valid", validator.isValid(whitespaceString, validatorContext));
    }
    
    @Test
    public void urlIsValid() {
      initializeValidator("urlString");     
      
      assertTrue("URL should be considered valid", validator.isValid(urlString, validatorContext));
    }

    @Test
    public void urnIsValid() {
      initializeValidator("urnString");     
      
      assertTrue("URN should be considered valid", validator.isValid(urnString, validatorContext));
    }
    
    @Test
    public void urnIsValidWhenSchemeMatches() {
      initializeValidator("uriWithMatchedScheme");     
      
      assertTrue("URI with matching scheme should be considered valid", validator.isValid(uriWithMatchedScheme, validatorContext));
    }
    
    @Test
    public void urnIsInvalidWhenSchemeMismatches() {
      initializeValidator("uriWithMismatchedScheme");     
      
      assertFalse("URI with mismatching scheme should be considered invalid", validator.isValid(uriWithMatchedScheme, validatorContext));
    }
}
