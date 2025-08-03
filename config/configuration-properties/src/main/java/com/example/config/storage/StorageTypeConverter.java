package com.example.config.storage;

import com.example.config.storage.pojos.StorageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StorageTypeConverter implements Converter<String, StorageType> {
  @Override
  public StorageType convert(String source) {
    return StorageType.fromValue(source);
  }
}
