FROM openjdk:17-jdk-alpine AS pre
WORKDIR /app
COPY cbpgext /app
RUN ./gradlew jar

FROM quay.io/keycloak/keycloak:20.0.1 AS final
COPY --from=pre /app/build/libs/cbpgext-*.jar /opt/keycloak/providers/cbpgext.jar