FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY shared ./shared
COPY services ./services
RUN mvn -q -DskipTests -pl services/llm-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/llm-service/target/llm-service-1.0.0.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
