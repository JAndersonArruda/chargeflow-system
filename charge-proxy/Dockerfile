FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/charge-proxy-0.0.1-SNAPSHOT.jar ./charge-proxy.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "charge-proxy.jar"]