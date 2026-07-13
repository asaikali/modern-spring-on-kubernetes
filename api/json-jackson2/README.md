# Jackson 2 JSON Processing Demo

This project demonstrates comprehensive **Jackson 2** (`com.fasterxml.jackson`) JSON processing capabilities through JUnit tests and a Spring Boot REST API. It showcases advanced Jackson features for JSON serialization, deserialization, and configuration in real-world scenarios.

Spring Boot 4 uses Jackson 3 by default, but Jackson 2 will remain widespread in the ecosystem for years; this module keeps the Jackson 2 versions of the samples working on Boot 4 via the `spring-boot-jackson2` bridge module and explicit Jackson 2 dependencies. See the sibling [json-jackson3](../json-jackson3/README.md) module for the same samples on Jackson 3 (`tools.jackson`).

## Features Overview

### 🚀 Spring Boot REST API

The project includes REST endpoints demonstrating Jackson's view-based serialization:

- `GET /user/public` - Returns User with public fields only (username)
- `GET /user/internal` - Returns User with all fields (username, email, secretNote)
- `POST /user/register` - Accepts User input with public view binding
- `POST /user/internal` - Accepts User input with internal view binding
- `GET /filter/public` - Dynamic filtering with SimpleBeanPropertyFilter
- `GET /filter/internal` - Dynamic filtering with all fields included

Test these endpoints using the provided `requests.http` file.

### ⚙️ Jackson Configuration

**Global ObjectMapper Customization** (`JacksonConfig.java`):
- Pretty printing for development
- ISO-8601 date formatting (disables timestamp serialization)
- Long-to-String serialization (JavaScript compatibility)
- Snake_case property naming strategy
- Custom modules integration

## Test Scenarios by Category

### 📋 Basic Data Binding

**Object Mapping & Serialization**
- `BasicObjectMappingTest` - Complex object graphs with nested records
- `BuilderDeserializationTest` - Builder pattern with `@JsonPOJOBuilder`
- `GenericTypesTest` - Generic type handling with `TypeReference`
- `JsonMergeTests` - Partial updates using `@JsonMerge`

### 🏷️ Field Mapping & Handling

**Property Control**
- `JsonAliasTest` - Multiple field names with `@JsonAlias`
- `MissingFieldTests` - Graceful handling of missing/extra fields
- `NullFieldSerializationTests` - Null field inclusion strategies
- `UnwrappedTests` - Object flattening with `@JsonUnwrapped`

**Dynamic Properties**
- `DynamicPropertiesTests` - Dynamic fields with `@JsonAnyGetter`/`@JsonAnySetter`
- `JacksonInjectTest` - Value injection with `@JacksonInject`

### 🎯 Presentation & Views

**Output Customization**
- `JsonViewTest` - Conditional field inclusion with views
- `JsonFilterTest` - Dynamic filtering with `@JsonFilter`
- `PropertyOrderTests` - Field ordering with `@JsonPropertyOrder`

### 🔄 Type Handling & Polymorphism

**Advanced Types**
- `PolymorphicTests` - Polymorphic serialization with `@JsonTypeInfo`
- `CustomSerializerTests` - Custom serializers/deserializers
- `DateTimeTests` - Date/time formatting with `@JsonFormat`

### 🌳 Object Graphs & References

**Complex Relationships**
- `CircularReferenceTest` - Circular references with `@JsonManagedReference`/`@JsonBackReference`
- `JsonIdentityInfoTest` - Object identity preservation with `@JsonIdentityInfo`

### 🌲 Tree Model Processing

**Dynamic JSON Handling**
- `TreeModelTests` - JsonNode manipulation for dynamic structures
- `TreeToValueConversionTests` - POJO ↔ JsonNode conversions
- `ConvertValueTest` - Object transformations via tree model

### 🔧 Configuration & Extensions

**Advanced Configuration**
- `ModuleTests` - Custom Jackson modules with serializers/deserializers
- `ObjectGraphMixinTest` - External annotation configuration with mixins
- `PrettyPrintTests` - Custom pretty printing strategies

### 📚 Versioning & Evolution

**Schema Evolution**
- `VersionedUserUnifiedTest` - Multi-version JSON support with custom deserializers

## Key Jackson Annotations Reference

### Property Mapping
- `@JsonProperty` - Field mapping and renaming
- `@JsonAlias` - Alternative property names
- `@JsonIgnore` / `@JsonIgnoreProperties` - Field exclusion
- `@JsonInclude` - Inclusion strategies (NON_NULL, NON_EMPTY)

### Views & Filtering
- `@JsonView` - Conditional field inclusion
- `@JsonFilter` - Dynamic filtering

### Object Relationships
- `@JsonManagedReference` / `@JsonBackReference` - Circular reference handling
- `@JsonIdentityInfo` - Object identity preservation

### Type Handling
- `@JsonTypeInfo` / `@JsonSubTypes` - Polymorphic type support
- `@JsonSerialize` / `@JsonDeserialize` - Custom serialization

### Structure Control
- `@JsonUnwrapped` - Object flattening
- `@JsonAnyGetter` / `@JsonAnySetter` - Dynamic properties
- `@JsonPropertyOrder` - Field ordering

### Advanced Features
- `@JsonCreator` - Custom constructors/factory methods
- `@JsonValue` - Single-value serialization
- `@JsonFormat` - Value formatting
- `@JacksonInject` - External value injection

## Getting Started

1. **Clone and run:**
   ```bash
   git clone <repository>
   cd json-jackson
   ./mvnw spring-boot:run
   ```

2. **Test REST endpoints:**
    - Use the provided `requests.http` file in your IDE
    - Or test manually: `curl http://localhost:8080/user/public`

3. **Explore test scenarios:**
   ```bash
   ./mvnw test
   ```

4. **Study specific features:**
    - Browse test packages by use case
    - Check `annotations.md` for comprehensive annotation reference

## Project Structure

```
src/
├── main/java/
│   ├── JacksonConfig.java           # Global ObjectMapper configuration
│   ├── jackson/
│   │   ├── views/                   # @JsonView examples
│   │   └── filter/                  # @JsonFilter examples
│   └── JsonJacksonApplication.java
└── test/java/
    └── jackson/
        ├── configuration/           # Global config & modules
        ├── databinding/            # Basic object mapping
        ├── objectgraph/            # Complex relationships
        ├── presentation/           # Output customization
        ├── tree/                   # Tree model processing
        └── versioning/             # Schema evolution
```

## Advanced Use Cases

This project demonstrates solutions for:

- **API Versioning** - Handle multiple JSON schema versions
- **Microservices** - Different data views for internal/external APIs
- **Frontend Integration** - JavaScript-friendly serialization
- **Data Migration** - Flexible field mapping during system evolution
- **Performance Optimization** - Custom serializers for specific requirements
- **Security** - Dynamic field filtering based on user permissions

## Further Reading

- [Jackson GitHub Repository](https://github.com/FasterXML/jackson)
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs)
- [Spring Boot Jackson Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.spring-mvc.customize-jackson-objectmapper)
