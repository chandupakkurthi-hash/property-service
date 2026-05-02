FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8082

COPY --from=build /app/target/property-service.jar property-service.jar

ENTRYPOINT ["java","-jar","property-service.jar"]
