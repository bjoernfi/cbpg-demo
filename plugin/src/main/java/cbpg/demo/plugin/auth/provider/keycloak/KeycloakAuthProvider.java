package cbpg.demo.plugin.auth.provider.keycloak;

import cbpg.demo.plugin.auth.*;
import cbpg.demo.plugin.auth.provider.AuthContext;
import cbpg.demo.plugin.auth.provider.AuthProvider;
import cbpg.demo.plugin.auth.session.SessionExpiredException;
import cbpg.demo.plugin.auth.session.SessionExpiredReason;
import cbpg.demo.plugin.common.JsonService;
import cbpg.demo.plugin.common.config.ConfigService;
import com.intellij.openapi.components.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;

@Service
public final class KeycloakAuthProvider implements AuthProvider {
    private final Http http;

    public KeycloakAuthProvider() {
        var keycloakConfig = buildKeycloakConfig();
        http = new Http(keycloakConfig, (params, headers) -> {
        });
    }

    @Override
    public AuthContext authenticate(String username, char[] password) {
        try {
            var authzClient = AuthzClient.create(buildKeycloakConfig());

            // This poses a security risk, yet the keycloak library requires the password as string
            var stringPassword = String.valueOf(password);

            var accessTokenResponse = authzClient.obtainAccessToken(username, stringPassword);
            return parseToken(accessTokenResponse);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == 401) {
                throw new InvalidCredentialsException();
            }

            throw ex;
        } catch (Exception ex) {
            throw new AuthCommunicationException(ex);
        }
    }

    @Override
    public boolean refreshRequired(AuthContext ctx) {
        var keycloakCtx = (KeycloakAuthContext) ctx;
        return keycloakCtx.isAccessTokenExpired();
    }

    public AuthContext refresh(AuthContext ctx) {
        var keycloakCtx = (KeycloakAuthContext) ctx;
        if (keycloakCtx.isRefreshTokenExpired()) {
            throw new SessionExpiredException(SessionExpiredReason.REFRESH_TOKEN_EXPIRED);
        }

        var configService = ConfigService.getInstance();
        var keycloak = configService.getConfig().keycloak();

        var refreshToken = keycloakCtx.getRefreshToken();
        var url = keycloak.url() + "/realms/" + keycloak.realm()
                + "/protocol/openid-connect/token";

        try {
            var accessTokenResponse = http.<AccessTokenResponse>post(url).authentication().client()
                    .form()
                    .param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                    .param("client_id", keycloak.clientId())
                    .param("client_secret", keycloak.clientSecret()).response()
                    .json(AccessTokenResponse.class).execute();
            return parseToken(accessTokenResponse);
        } catch (Exception ex) {
            if (ex instanceof HttpResponseException e) {
                var body = new String(e.getBytes(), StandardCharsets.UTF_8);
                var json = JsonService.getInstance();
                var error = json.parse(body, HashMap.class);
                if (error.get("error").equals("invalid_grant")) {
                    // refresh token was probably invalidated due to a password change
                    throw new SessionExpiredException(SessionExpiredReason.REFRESH_TOKEN_INVALID);
                }
            }

            throw new AuthCommunicationException(ex);
        }
    }

    private Configuration buildKeycloakConfig() {
        var configService = ConfigService.getInstance();
        var conf = new Configuration(
                configService.getConfig().keycloak().url(),
                configService.getConfig().keycloak().realm(),
                configService.getConfig().keycloak().clientId(),
                Map.of("secret", configService.getConfig().keycloak().clientSecret()),
                null
        );
        conf.setConnectionTimeout(configService.getConfig().keycloak().timeout() * 1000L);
        conf.setSocketTimeout(configService.getConfig().keycloak().timeout() * 1000L);
        return conf;
    }

    private KeycloakAuthContext parseToken(AccessTokenResponse accessTokenResponse) {
        var accessToken = accessTokenResponse.getToken();
        var accessTokenExpiration = Instant.now().plusSeconds(accessTokenResponse.getExpiresIn());
        var refreshToken = accessTokenResponse.getRefreshToken();
        var refreshTokenExpiration = Instant.now()
                .plusSeconds(accessTokenResponse.getRefreshExpiresIn());

        var token = new KeycloakAuthContext();
        token.setAccessToken(accessToken);
        token.setAccessTokenExpiration(accessTokenExpiration);
        token.setRefreshToken(refreshToken);
        token.setRefreshTokenExpiration(refreshTokenExpiration);

        var json = JsonService.getInstance();
        var chunks = accessToken.split("\\.");
        var decoder = Base64.getUrlDecoder();
        var payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
        var payloadDict = json.parse(payload, HashMap.class);

        var name = (String) payloadDict.get("name");
        var loginName = (String) payloadDict.get("preferred_username");

        // if the consent attribute does not exist for that user
        // we assume consent=true (for demo purposes)
        var consentRaw = (String) payloadDict.get("consent");
        var consent = consentRaw == null || consentRaw.toLowerCase().equals("true");

        token.setName(name);
        token.setLoginName(loginName);
        token.setConsented(consent);

        return token;
    }

}
