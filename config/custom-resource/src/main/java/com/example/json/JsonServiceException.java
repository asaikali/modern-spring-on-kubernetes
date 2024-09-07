/*
 * Copyright 2023 Programming Mastery Inc.
 *
 * All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * Proprietary and confidential
 */

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
