package org.trentbowman.purple_viper.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trentbowman.purple_viper.domain.Asset;
import org.trentbowman.purple_viper.domain.AssetRepository;
import org.trentbowman.purple_viper.domain.Note;

@RestController
@RequestMapping("assets")
public class AssetsController {
  
  private final AssetRepository assetRepository;
  
  public AssetsController(AssetRepository assetRepository) {
    
    this.assetRepository = assetRepository;
  }
  
  @GetMapping
  public List<Asset> listAssets() {
    List<Asset> assets = new ArrayList<>();
    for (Asset asset : assetRepository.findAll()) {
      assets.add(asset);
    }
    Map<String, Object> result = new HashMap<>();
    result.put("assets", assets);
    return assets;
  }

  @PostMapping
  public Asset createAsset(@Valid @RequestBody Asset asset, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw AssetNotValidException.fromBindingResult(bindingResult);
    }
    asset.setId(UUID.randomUUID().toString());
    return assetRepository.save(asset);
  }

  @GetMapping("/{id}")
  public Asset retrieveAsset(@PathVariable String id) {
    Asset asset = assetRepository.findOne(id);
    if (asset == null) { throw new AssetNotFoundException(id); }
    return asset;
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAsset(@PathVariable String id) {
    Asset asset = assetRepository.findOne(id);
    if (asset == null) { throw new AssetNotFoundException(id); }
    
    assetRepository.delete(asset);
  }

  @PostMapping("/{assetId}/notes")
  public Note createNote(@PathVariable String assetId, @RequestBody Note note) {
    Asset asset = assetRepository.findOne(assetId);
    if (asset == null) { throw new AssetNotFoundException(assetId); }

    asset.addNote(note);
    assetRepository.save(asset);
    return note;
  }

}
