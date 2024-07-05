FROM eclipse-temurin:21-jre as builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination /out --application-filename application.jar

FROM eclipse-temurin:21-jre
WORKDIR /application
COPY --from=builder /out/dependencies/ ./
COPY --from=builder /out/snapshot-dependencies/ ./
# COPY --from=builder /out/spring-boot-loader/ ./ not needed anymore always empty
COPY --from=builder /out/application/ ./
ENTRYPOINT ["java", "-jar", "application.jar"]
