# TODO: consider alpine
FROM gradle:7.6.0-jdk17-focal
RUN apt-get update && apt-get -y install glpk-utils
WORKDIR /app

# Dependencies
COPY build.gradle settings.gradle ./
RUN gradle build 2>/dev/null || true

# Build
ADD . ./
RUN gradle assemble

# Run
ENTRYPOINT java -jar build/libs/*SNAPSHOT.jar
