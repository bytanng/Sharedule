# Step 1: Build the application using OpenJDK 21 and Maven
FROM openjdk:21-jdk-slim AS build

# Install Maven in the build stage
RUN apt-get update && apt-get install -y maven

# ✅ Set the working directory to root since pom.xml is in the root
WORKDIR /

# ✅ Copy only pom.xml first (for better caching)
# Ensure test files are included in the build stage
COPY src/ /src/
COPY pom.xml /pom.xml

# Run tests before packaging
RUN mvn clean test


# ✅ Download dependencies early (faster builds)
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY . .

# Build the application with Maven (skip tests to speed up the build)
RUN mvn clean package -DskipTests

# Step 2: Use a fresh OpenJDK 21 image to run the application
FROM openjdk:21-jdk-slim

# ✅ Set the working directory to root where pom.xml and target exist
WORKDIR /

# Copy built JAR from the build stage
COPY --from=build /target/sharedule-0.0.1-SNAPSHOT.jar /target/ShareduleApplication.jar

# Copy `pom.xml` for testing
COPY --from=build /pom.xml /pom.xml

# Install Maven in the final container (if you want to run tests inside)
RUN apt-get update && apt-get install -y maven

# Expose the port for Spring Boot
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/target/ShareduleApplication.jar"]
