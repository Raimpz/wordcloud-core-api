FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN chmod +x ./gradlew

COPY src ./src

RUN ./gradlew clean bootJar -x test

FROM gcr.io/distroless/java21-debian12
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
