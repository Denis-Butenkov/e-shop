FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/eshop-backend-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]