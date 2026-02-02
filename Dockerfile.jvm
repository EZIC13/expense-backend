FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -e -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/quarkus-app /app

EXPOSE 8080

CMD ["java", "-jar", "/app/quarkus-run.jar"]