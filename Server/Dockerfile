FROM amazoncorretto:23.0.0-alpine3.20

# Set working directory
WORKDIR /app

# Copy all files from the current directory to /app in the container
COPY ./ /app

# Ensure the gradlew file is executable
RUN chmod +x gradlew


# Expose the port your Spring Boot app will run on
EXPOSE 8080

# Run the Spring Boot app using gradle wrapper
ENTRYPOINT ["./gradlew", "bootRun"]

