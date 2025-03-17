package cbpg.demo.plugin.auth.provider.keycloak;

import cbpg.demo.plugin.auth.provider.AuthContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.time.LocalDateTime;

public class KeycloakAuthContext implements AuthContext {
    private String accessToken;
    private Instant accessTokenExpiration;
    private String refreshToken;
    private Instant refreshTokenExpiration;

    private String loginName;
    private String name;
    private boolean consent;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Instant accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Instant refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasConsent() {
        return consent;
    }

    public void setConsented(boolean consent) {
        this.consent = consent;
    }

    @JsonIgnore
    public boolean isAccessTokenExpired() {
        Instant now = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        return !(now.isBefore(getAccessTokenExpiration()) && now.isBefore(
                getRefreshTokenExpiration()));
    }

    @JsonIgnore
    public boolean isRefreshTokenExpired() {
        Instant now = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        return !now.isBefore(getRefreshTokenExpiration());
    }
}
