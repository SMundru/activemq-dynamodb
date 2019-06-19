# Build stage

FROM maven:3.6.1-jdk-8-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean compile assembly:single

# Package stage

FROM openjdk:8-jre-slim
COPY --from=build "/home/app/target/amazonmq-dynamodb-1.0-SNAPSHOT.jar" "/usr/local/lib/amazonmq-dynamodb-1.0-SNAPSHOT.jar"
# 3000 is the default server port
EXPOSE 3000
ENTRYPOINT [ "java", "-jar", "/usr/local/lib/amazonmq-dynamodb-1.0-SNAPSHOT.jar" ]