FROM gradle:7.6.0-jdk17-alpine
WORKDIR /app

# Dependencies
COPY build.gradle settings.gradle ./
RUN gradle build 2>/dev/null || true

# Build
ADD . ./
RUN gradle assemble

# Run
ENTRYPOINT java -jar build/libs/*SNAPSHOT.jar
