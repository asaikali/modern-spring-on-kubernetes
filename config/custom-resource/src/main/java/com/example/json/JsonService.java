package com.example.json;

/**
 * Service for working with JSON strings. This service provides methods for formatting, parsing,
 * serializing/deserializing objects. . Dates and times are configured to work with java.time
 * package to nanosecond precision.
 */
public interface JsonService {

  /**
   * Formats a JSON String by adding indentation to the string.
   *
   * @param json the string json object
   * @return the formatted json string
   */
  String format(String json);

  /**
   * Converts a string to an instance of a Java object of the specified type.
   *
   * @param json the json string to convert to a java object
   * @param type the class of the java object that the json string be converted to
   * @param <T> the tye of the java object to convert the json string to
   * @return an instance of the java object of the specified type
   */
  <T> T fromJson(String json, Class<T> type);

  /**
   * Returns a string json string from a java object.
   *
   * @param object the java object to turn into a json string
   * @return a string json representation of the java object
   */
  String toJson(Object object);
}
