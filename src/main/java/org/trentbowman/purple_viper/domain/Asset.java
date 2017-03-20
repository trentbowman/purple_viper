package org.trentbowman.purple_viper.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.couchbase.core.mapping.Document;
import org.trentbowman.purple_viper.validator.constraints.URI;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

@Document
public class Asset {

  @Id
  private String id;

  @Field
  @NotBlank(message="uri is required")
  @URI(scheme="asset", message="uri must be a valid URI with a scheme of \"asset\"")
  private String uri;

  @Field
  @NotBlank(message="name is required")
  private String name;

  @Field
  private List<Note> notes;

  public Asset() {};

  public Asset(String id, String uri, String name, List<Note> notes) {
    super();
    this.id = id;
    this.uri = uri;
    this.name = name;
    this.notes = notes;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public List<Note> getNotes() {
    return notes;
  }

  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  public void addNote(Note note) {
    if (getNotes() == null) {
      setNotes(new ArrayList<>());
    }
    
    getNotes().add(note);
  }
 
}
