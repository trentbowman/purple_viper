package org.trentbowman.purple_viper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.trentbowman.purple_viper.domain.AssetRepository;
import org.trentbowman.purple_viper.web.AssetsController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

  @Autowired
  private AssetsController assetsController;
  
  @Autowired
  private AssetRepository assetRepository;

  @Test
  public void contextLoads() {
    assertThat(assetsController).isNotNull();
    assertThat(assetRepository).isNotNull();
  }

}
