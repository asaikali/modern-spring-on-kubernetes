## 📚 Jackson Learning Scenarios (Progressive Table)

| #  | Scenario                                                   | Description                                                                 | Key APIs / Concepts                          |
|----|------------------------------------------------------------|-----------------------------------------------------------------------------|----------------------------------------------|
| 1  | Basic POJO serialization                                   | Serialize a simple Java object to JSON                                      | `ObjectMapper.writeValueAsString()`          |
| 2  | Basic POJO deserialization                                 | Deserialize JSON into a Java object                                         | `ObjectMapper.readValue()`                   |
| 3  | Ignoring unknown fields                                    | Deserialize JSON with extra fields using `@JsonIgnoreProperties`            | `@JsonIgnoreProperties`                      |
| 4  | Field name customization                                   | Map JSON fields to differently named Java fields using `@JsonProperty`      | `@JsonProperty`                              |
| 5  | Skipping null or default values                            | Avoid serializing nulls using `@JsonInclude`                                | `@JsonInclude`                               |
| 6  | Custom constructor + validation                            | Use `@JsonCreator` and validate input in constructor                        | `@JsonCreator`, `@JsonProperty`              |
| 7  | Serialize Java `List` and `Map`                            | Handle collections (arrays, maps)                                           | Generic binding                              |
| 8  | Read/write to tree model                                   | Use `JsonNode` to manipulate unknown or dynamic JSON                        | `readTree()`, `writeTree()`, `JsonNode`      |
| 9  | Mix-ins to externalize annotations                         | Apply annotations without modifying the original class                      | Mix-in annotations + `ObjectMapper.addMixIn` |
| 10 | Custom serializer/deserializer                             | Define behavior for unusual field formats or types                          | `JsonSerializer`, `JsonDeserializer`         |
| 11 | Enum customization                                         | Customize enum names or values during serialization                         | `@JsonValue`, `@JsonCreator` on enums        |
| 12 | Polymorphic types                                          | Deserialize supertype to specific subtype using `@JsonTypeInfo`             | `@JsonTypeInfo`, `@JsonSubTypes`             |
| 13 | Flattening nested structures                               | Flatten/unflatten nested objects with `@JsonUnwrapped`                      | `@JsonUnwrapped`                             |
| 14 | Dynamic properties (Map-backed objects)                    | Deserialize/serialize arbitrary key-value pairs                             | `@JsonAnyGetter`, `@JsonAnySetter`           |
| 15 | Modules for Java 8, Java Time, Kotlin, etc.                | Handle optional types, `LocalDate`, etc. with proper modules                | `jackson-datatype-jsr310`, `jackson-module-kotlin` |
| 16 | Schema-based validation (external)                         | Validate JSON against a JSON Schema                                         | External libs: Everit, NetworkNT             |
| 17 | Afterburner for performance                                | Optimize serialization performance using bytecode enhancement               | `jackson-module-afterburner`                 |
| 18 | Streaming read/write                                       | Parse/generate large JSON using `JsonParser` and `JsonGenerator`           | `jackson-core`                               |
| 19 | Bidirectional conversion with `treeToValue()`              | Map between `JsonNode` and POJOs                                            | `treeToValue()`, `valueToTree()`             |
| 20 | Patch/merge object trees                                   | Apply partial updates to JSON                                              | `JsonMerge`, `ObjectReader.readerForUpdating()` |
