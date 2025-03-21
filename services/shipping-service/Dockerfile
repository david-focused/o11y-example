FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY settings.gradle /app
COPY common-domain /app/common-domain
COPY services/shipping-service /app/services/shipping-service
RUN chmod +x /app/services/shipping-service/gradlew

RUN ./services/shipping-service/gradlew clean bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/services/shipping-service/build/libs/shipping-service-0.0.1-SNAPSHOT.jar /app/shipping-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "shipping-service.jar"]