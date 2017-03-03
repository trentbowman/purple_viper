package org.trentbowman.purple_viper.couchbase;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

  @Value("${couchbase.cluster.bucket:default}")
  private String bucketName;

  @Value("${couchbase.cluster.password:}")
  private String password;

  @Value("${couchbase.cluster.ip:127.0.0.1}")
  private String ip;

  @Override
  protected List<String> getBootstrapHosts() {
    return Arrays.asList(ip);
  }
  
  @Override
  protected String getBucketName() {
    return bucketName;
  }

  @Override
  protected String getBucketPassword() {
    return password;
  }

}