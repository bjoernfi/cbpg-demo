FROM openjdk:17-jdk-alpine AS pre
WORKDIR /app
COPY . /app
RUN apk add --no-cache tzdata
RUN ./gradlew bootJar

FROM openjdk:17-jdk-alpine AS final
COPY --from=pre /app/build/libs/backend-*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]