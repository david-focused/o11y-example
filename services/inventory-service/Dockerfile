FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY settings.gradle /app
COPY common-domain /app/common-domain
COPY services/inventory-service /app/services/inventory-service
RUN chmod +x /app/services/inventory-service/gradlew

RUN ./services/inventory-service/gradlew clean bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/services/inventory-service/build/libs/inventory-service-0.0.1-SNAPSHOT.jar app.jar

# Download OpenTelemetry Java agent
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.14.0/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar

EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]