FROM eclipse-temurin:21-jre as builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted --application-filename app.jar
RUN java -Xshare:dump
RUN java  -Dspring.context.exit=onRefresh -XX:ArchiveClassesAtExit=/application.jsa -jar extracted/application/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /application
COPY --from=builder /extracted/application/dependencies/ ./
COPY --from=builder /extracted/application/spring-boot-loader/ ./
COPY --from=builder /extracted/application/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/application/ ./
COPY --from=builder /application.jsa /application.jsa
ENTRYPOINT ["java","-Dspring.aot.enabled=true","-XX:SharedArchiveFile=/application.jsa","-jar","/application/app.jar"]
