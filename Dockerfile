FROM eclipse-temurin:25-jdk AS builder

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon
RUN cp build/libs/*.jar app.jar

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /workspace/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
