FROM gradle:8.7-jdk21 AS builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre

RUN addgroup --system appgroup && adduser --system --no-create-home --uid 1001 appuser --ingroup appgroup

WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/build/libs/api-calculator-0.0.1-SNAPSHOT.jar /app/api-calculator.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+ExitOnOutOfMemoryError", "-Dspring.profiles.active=docker", "-Djava.security.egd=file:/dev/urandom", "-jar", "/app/api-calculator.jar"]

