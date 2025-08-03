package com.example.config.storage.pojos;

public class ProviderSettings {
  private LocalProvider local = new LocalProvider();
  private S3Provider s3 = new S3Provider();
  private GcsProvider gcs = new GcsProvider();

  public LocalProvider getLocal() {
    return local;
  }

  public ProviderSettings setLocal(LocalProvider local) {
    this.local = local;
    return this;
  }

  public S3Provider getS3() {
    return s3;
  }

  public ProviderSettings setS3(S3Provider s3) {
    this.s3 = s3;
    return this;
  }

  public GcsProvider getGcs() {
    return gcs;
  }

  public ProviderSettings setGcs(GcsProvider gcs) {
    this.gcs = gcs;
    return this;
  }
}
