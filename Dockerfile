FROM eclipse-temurin:20-jdk as build

WORKDIR .

COPY . .

RUN ./gradlew bootJar

FROM bellsoft/liberica-openjdk-alpine-musl:20 as main

WORKDIR .

COPY --from=build build/libs/task-manager-api-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "task-manager-api-0.0.1-SNAPSHOT.jar"]
