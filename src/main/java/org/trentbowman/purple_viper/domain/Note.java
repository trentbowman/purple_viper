package org.trentbowman.purple_viper.domain;

public class Note {

  private String text;

  public Note() {};
  
  public Note(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
