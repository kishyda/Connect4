FROM amazoncorretto:23.0.0-alpine3.20

WORKDIR /app

COPY . .

RUN chmod +x gradlew

# List the files in the container's /app directory for debugging
RUN ls -la /app

CMD ["./gradlew", "bootRun"]

