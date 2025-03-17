package cbpg.demo.cbpgext;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import java.util.Objects;
import java.util.stream.Collectors;

public class TokenInvalidationListener implements EventListenerProvider {
    private static final Logger LOGGER = Logger.getLogger(TokenInvalidationListener.class);
    private final KeycloakSession session;

    public TokenInvalidationListener(KeycloakSession session) {
        this.session = session;
    }


    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.UPDATE_PASSWORD) {
            var realm = session.getContext().getRealm();
            var authSession = session.getContext().getAuthenticationSession();

            if(authSession != null) {
                var user = authSession.getAuthenticatedUser();

                if (user != null) {
                    // invalidate all sessions except the one that was used for updating the password
                    var userSessions = session.sessions().getUserSessionsStream(realm, user)
                            .filter(s -> !Objects.equals(s.getId(), authSession.getParentSession().getId()))
                            .collect(Collectors.toList());

                    for (var userSession : userSessions) {
                        session.sessions().removeUserSession(realm, userSession);
                    }

                    LOGGER.info(String.format(
                            "Invalidated tokens for user %s (id %s)",
                            user.getUsername(),
                            user.getId()
                    ));
                }
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {

    }

    @Override
    public void close() {

    }
}
