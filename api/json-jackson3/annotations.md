# Jackson Annotations by use case 

## Property Mapping

Used to control how individual properties are serialized and deserialized.

| Annotation            | Purpose                                                             |
|-----------------------|---------------------------------------------------------------------|
| `@JsonProperty`       | Rename a field or explicitly mark it for inclusion.                |
| `@JsonAlias`          | Support alternative names for a property during deserialization.   |
| `@JsonIgnore`         | Ignore this field for both serialization and deserialization.      |
| `@JsonIgnoreProperties` | Ignore multiple fields globally or on a class.                   |
| `@JsonInclude`        | Control inclusion rules (e.g. `NON_NULL`, `NON_EMPTY`).             |
| `@JsonSetter`         | Customize the setter for a property (deserialization).             |
| `@JsonGetter`         | Customize the getter for a property (serialization).               |
| `@JsonAutoDetect`     | Change default visibility (public, private, etc.).                 |
| `@JsonPropertyOrder`  | Specify the order of properties during serialization.              |


## Object Identity / References

Used to handle circular references or shared object identity.

| Annotation              | Purpose                                                                 |
|-------------------------|-------------------------------------------------------------------------|
| `@JsonManagedReference` | Used with `@JsonBackReference` to manage parent-child relationships.    |
| `@JsonBackReference`    | Prevents infinite recursion in bidirectional relationships.             |
| `@JsonIdentityInfo`     | Adds object identity (`@id` references) for circular/shared references. |
| `@JsonIdentityReference`| Force serialization as ID (not full object) when using `@JsonIdentityInfo`. |


## Polymorphic Type Handling

Support for abstract types and runtime type resolution.

| Annotation          | Purpose                                                       |
|---------------------|---------------------------------------------------------------|
| `@JsonTypeInfo`     | Include type information for polymorphic deserialization.     |
| `@JsonSubTypes`     | List known subtypes for an abstract class/interface.          |
| `@JsonTypeName`     | Define the name used for a specific subtype.                  |
| `@JsonTypeResolver` | Use a custom type resolver.                                   |
| `@JsonTypeId`       | Customize how type ID is resolved during polymorphic handling.|


## Views / Projections

Control which fields are included for a given “view”.

| Annotation  | Purpose                                                   |
|-------------|-----------------------------------------------------------|
| `@JsonView` | Include/exclude fields based on active view class.        |

## Unwrapping and Wrapping

Control how nested objects or additional fields are handled.

| Annotation         | Purpose                                                                      |
|--------------------|------------------------------------------------------------------------------|
| `@JsonUnwrapped`   | Flatten a nested object’s properties into the parent.                        |
| `@JsonAnyGetter`   | Include a `Map<String, Object>` as additional fields during serialization.   |
| `@JsonAnySetter`   | Capture unknown fields into a map during deserialization.                    |
| `@JsonRawValue`    | Embed raw JSON content (skip escaping).                                      |

## Custom Deserialization / Serialization

Allow custom (de)serialization logic via classes or methods.

| Annotation           | Purpose                                                              |
|----------------------|----------------------------------------------------------------------|
| `@JsonDeserialize`   | Assign a custom deserializer for a field or class.                   |
| `@JsonSerialize`     | Assign a custom serializer for a field or class.                     |
| `@JsonCreator`       | Define constructors or factory methods used during deserialization.  |
| `@JsonValue`         | Marks a single method/field as the serialized form.                  |
| `@JsonPOJOBuilder`   | Define how to use a builder class for deserialization.               |

## Injection and External Configuration

Support for dependency injection or decoupled annotations.

| Annotation               | Purpose                                                                |
|--------------------------|------------------------------------------------------------------------|
| `@JacksonInject`         | Inject external values into the object during deserialization.         |
| `@JsonIncludeProperties` | Only include listed fields (opposite of `@JsonIgnoreProperties`).      |
| `@JsonFilter`            | Used with `FilterProvider` to dynamically include/exclude fields.      |
| `@JsonRootName`          | Rename root element (for XML or root wrapping).                        |
| `@JsonNaming`            | Use a naming strategy (e.g., `snake_case`) for field names.            |
| `@JsonMixin`             | Configure annotations on external types via mixin.                     |

## Format and Constraints

Used to define formatting, parsing, or validation rules.

| Annotation             | Purpose                                                                 |
|------------------------|-------------------------------------------------------------------------|
| `@JsonFormat`          | Control date/time or number formatting.                                |
| `@JsonEnumDefaultValue`| Define default value for enums when unknown value is encountered.       |

