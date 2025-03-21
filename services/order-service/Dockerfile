FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY settings.gradle /app
COPY common-domain /app/common-domain
COPY services/order-service /app/services/order-service
RUN chmod +x /app/services/order-service/gradlew
RUN ./services/order-service/gradlew clean bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/services/order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar /app/order-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "order-service.jar"]
