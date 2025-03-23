# Step 1: Build the application using OpenJDK 21 and Maven installed manually
FROM openjdk:21-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory to /src
WORKDIR /src

# Copy the project files into the container
COPY . .

# Build the application with Maven (skip tests to speed up the build)
RUN mvn clean package -DskipTests

RUN ls -l /src/target

# Step 2: Use a fresh OpenJDK 21 image to run the application
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /

# Copy the built application (JAR) from the build stage
# The JAR file is located in /target directory after the build process
COPY --from=build /src/target/sharedule-0.0.1-SNAPSHOT.jar /target/ShareduleApplication.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/target/ShareduleApplication.jar"]
