FROM alpine:3.21.2 AS pre
RUN apk add --no-cache jq
WORKDIR /conf
COPY . /conf
RUN ./compile.sh

FROM adorsys/keycloak-config-cli:latest-20.0.1 AS final
COPY --from=pre /conf/out/*.json /config/