# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY openapi.properties /workspace
COPY src /workspace/src
RUN mvn clean package

# Stage 2: Create the image
FROM eclipse-temurin:21-alpine
RUN mkdir -p /app/.key-pair
COPY --from=build /workspace/target/k1te-auth-*.jar app.jar
COPY local-config.yml /app/local-config.yml
COPY .key-pair/ec512-key-pair.pem /app/.key-pair/ec512-key-pair.pem
RUN chmod 600 /app/.key-pair/ec512-key-pair.pem
ENV MICRONAUT_CONFIG_FILES=/app/local-config.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]