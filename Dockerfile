# JVM BUILD
#FROM maven:3.9.7-eclipse-temurin-21 AS build
#WORKDIR /workspace
#
#COPY pom.xml .
#COPY src ./src
#
#RUN mvn -e -DskipTests package
#
#FROM eclipse-temurin:21-jre-jammy
#WORKDIR /app
#
#COPY --from=build /workspace/target/quarkus-app /app
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "/app/quarkus-run.jar"]

# NATIVE BUILD
# -------- Build stage (native) --------
FROM quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21 AS build
WORKDIR /code

# Copy and run the Maven wrapper
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -B dependency:go-offline

# Copy source and build native
COPY src ./src
RUN ./mvnw package -Pnative -DskipTests

# -------- Runtime stage --------
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0
WORKDIR /app
COPY --from=build /code/target/*-runner /app/application

EXPOSE 8080
ENTRYPOINT ["/app/application"]