FROM openjdk:17-jdk-alpine AS pre
WORKDIR /app
COPY pwdchangeldap /app
RUN ./gradlew jar

FROM quay.io/keycloak/keycloak:20.0.1 AS final
COPY --from=pre /app/build/libs/pwdchangeldap-*.jar /opt/keycloak/providers/pwdchangeldap.jar