# FROM eclipse-temurin:17-jre-alpine
FROM amazoncorretto:25-alpine
COPY target/DWES25-26.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
