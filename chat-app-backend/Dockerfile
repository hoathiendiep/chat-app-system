FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk
ARG JAR_FILE=/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]