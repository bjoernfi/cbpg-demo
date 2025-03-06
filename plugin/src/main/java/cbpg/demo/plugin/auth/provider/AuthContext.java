package cbpg.demo.plugin.auth.provider;

import cbpg.demo.plugin.auth.provider.keycloak.KeycloakAuthContext;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KeycloakAuthContext.class, name = "keycloak"),
})
public interface AuthContext {
    String getLoginName();
    String getName();
    boolean hasConsent();
}
