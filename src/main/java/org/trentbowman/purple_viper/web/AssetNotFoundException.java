package org.trentbowman.purple_viper.web;

public class AssetNotFoundException extends RuntimeException {
  public AssetNotFoundException(String assetId) {
    super("no asset with id " + assetId + " found");
  }
}
