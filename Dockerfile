# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Add labels
LABEL maintainer="Hair Care E-commerce"
LABEL version="1.0.0"

# Copy the built JAR from build stage
COPY --from=build /build/target/ecommerce-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]