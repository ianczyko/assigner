# alpine image would be lighter but cplex seems to have troubles with it
FROM gradle:7.6.0-jdk17-focal
WORKDIR /app

# Install cplex
COPY installers/cplex_studio.bin ./installers/
COPY installers/installer.properties ./installers/
RUN installers/cplex_studio.bin -i silent
RUN cp /opt/ibm/ILOG/CPLEX_Studio2211/cplex/bin/x86-64_linux/* /usr/lib

# Dependencies
COPY build.gradle settings.gradle ./
RUN gradle build 2>/dev/null || true

# Build
ADD . ./
RUN gradle assemble

# Run
ENTRYPOINT java -jar build/libs/*SNAPSHOT.jar
