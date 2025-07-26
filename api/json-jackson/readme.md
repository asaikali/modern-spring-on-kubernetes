# Jackson JSON Processing Demo

This project demonstrates various Jackson JSON processing scenarios through JUnit tests and a Spring Boot REST API. It showcases how to use Jackson for JSON serialization and deserialization in different contexts.

## Spring Boot REST API

The project includes a simple Spring Boot REST API that demonstrates the use of Jackson's `@JsonView` annotation to control which fields are included in the JSON response:

- `GET /user/public` - Returns a User object with only the public fields (username)
- `GET /user/internal` - Returns a User object with all fields (username, email, secretNote)

You can test these endpoints using the provided `requests.http` file.

## Jackson Configuration

The project includes a `JacksonConfig` class that demonstrates how to customize the Jackson ObjectMapper in a Spring Boot application:

- Enabling pretty printing for better readability
- Disabling writing dates as timestamps (using ISO-8601 format instead)
- Registering a custom module to serialize Long values as strings (to avoid precision issues in JavaScript)
- Setting a property naming strategy (snake_case)

## JUnit Test Scenarios

The project includes a comprehensive set of JUnit tests that demonstrate various Jackson features:

### Basic Serialization and Deserialization
- **JsonViewTest**: Demonstrates how to use `@JsonView` to conditionally include fields in JSON output
- **NullFieldSerializationTests**: Shows how to control serialization of null fields
- **PrettyPrintTests**: Demonstrates how to format JSON output with pretty printing

### Custom Serialization and Deserialization
- **CustomSerializerTests**: Shows how to create custom serializers and deserializers for Java types
- **DateTimeTests**: Demonstrates how to handle date and time values in JSON

### Advanced Object Mapping
- **PolymorphicTests**: Shows how to handle polymorphic types with `@JsonTypeInfo` and `@JsonSubTypes`
- **ObjectGraphTest**: Demonstrates serialization and deserialization of complex object graphs
- **ObjectGraphMixinTest**: Shows how to use mix-ins to externalize annotations
- **UnwrappedTests**: Demonstrates how to flatten nested objects with `@JsonUnwrapped`

### Dynamic JSON Processing
- **TreeModelTests**: Shows how to use the tree model (`JsonNode`) to manipulate JSON
- **TreeToValueConversionTests**: Demonstrates conversion between POJOs and the tree model
- **DynamicPropertiesTests**: Shows how to handle dynamic properties with `@JsonAnyGetter` and `@JsonAnySetter`

### Collections and Generic Types
- **GenericTypesTest**: Demonstrates how to handle generic types in JSON
- **JsonMergeTests**: Shows how to merge JSON objects

### Error Handling and Edge Cases
- **MissingFieldTests**: Demonstrates how to handle missing fields during deserialization
- **ModuleTests**: Shows how to use Jackson modules for additional functionality

## Getting Started

1. Clone the repository
2. Run the application with `./mvnw spring-boot:run`
3. Test the REST API endpoints using the provided `requests.http` file
4. Explore the JUnit tests to understand the different Jackson features

## Further Reading

For more information on Jackson, refer to the [Jackson GitHub repository](https://github.com/FasterXML/jackson) and the [Jackson documentation](https://github.com/FasterXML/jackson-docs).
