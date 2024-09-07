package com.example.json;

/**
 * Indicates an error parsing a json string into a java object, or serializing a java object into a
 * json string.
 */
public class JsonServiceException extends RuntimeException {

  public JsonServiceException(String message, Throwable e) {
    super(message, e);
  }
}
