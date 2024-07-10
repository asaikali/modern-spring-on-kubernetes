# SBOM

This app demos the Software Bill of Materials (SBOM) feature introduced in
Spring Boot 3.3.

1. The SBOM is generated when the application is built so start by running
   `mvn package` to get the sbom generated.

2. Visit [http://localhost:8080/actuator/sbom](http://localhost:8080/actuator/sbom) to see what types of SBOMs are
   available.

2. Visit [http://localhost:8080/actuator/sbom/application](http://localhost:8080/actuator/sbom/application)
   to view the SBOM of the application itself.

## Resources

* Blog post [SBOM support in Spring Boot 3.3](https://spring.io/blog/2024/05/24/sbom-support-in-spring-boot-3-3)
