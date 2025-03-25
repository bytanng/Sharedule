# Step 1: Build the application using OpenJDK 21 and Maven installed manually
FROM openjdk:21-jdk-slim AS build

# Install Maven in the build stage
RUN apt-get update && apt-get install -y maven

# Set the working directory to /src
WORKDIR /src

# ✅ Copy only pom.xml first (for better caching)
COPY pom.xml .

# ✅ Download dependencies early (faster builds)
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY . .

# Build the application with Maven (skip tests to speed up the build)
RUN mvn clean package -DskipTests

# Step 2: Use a fresh OpenJDK 21 image to run the application
FROM openjdk:21-jdk-slim

# ✅ Set the working directory where pom.xml exists
WORKDIR /src

# Copy built JAR
COPY --from=build /src/target/sharedule-0.0.1-SNAPSHOT.jar /target/ShareduleApplication.jar

# Copy `pom.xml` for testing
COPY --from=build /src/pom.xml /target/pom.xml

# Install Maven in the final container
RUN apt-get update && apt-get install -y maven

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/target/ShareduleApplication.jar"]
