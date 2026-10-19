# Stage 1: Build the application with Maven
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy Maven wrapper and POM to leverage Docker cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies offline
RUN mvn dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create lightweight production image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create upload directory for attachments
RUN mkdir -p /app/uploads/attachments

# Copy the built JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose server port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
