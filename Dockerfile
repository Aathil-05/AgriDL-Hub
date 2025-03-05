# Use OpenJDK base image
FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/AgriML-Hub-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
