FROM eclipse-temurin:24-jre as builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --destination /out --application-filename app.jar

FROM eclipse-temurin:24-jre
WORKDIR /application
COPY --from=builder out/lib ./lib
COPY --from=builder out/app.jar ./
RUN java -Dspring.aot.enabled=true -Dspring.context.exit=onRefresh -XX:ArchiveClassesAtExit=/application/app.jsa -jar /application/app.jar
ENTRYPOINT ["java","-Dspring.aot.enabled=true", "-XX:SharedArchiveFile=/application/app.jsa","-jar","/application/app.jar"]
