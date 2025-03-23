# Step 2: Use a fresh OpenJDK 21 image to run the application
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built application (JAR) from the build stage
COPY --from=build /src/target/sharedule-0.0.1-SNAPSHOT.jar /app/ShareduleApplication.jar

# Copy the .env file into the container (if needed)
COPY .env /app/.env

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/ShareduleApplication.jar"]
