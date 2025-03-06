package cbpg.demo.plugin.auth;

import cbpg.demo.plugin.auth.consent.ConsentContext;
import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.auth.consent.NoConsentException;
import cbpg.demo.plugin.auth.provider.AuthContext;
import cbpg.demo.plugin.auth.provider.AuthProvider;
import cbpg.demo.plugin.auth.session.SessionExpiredException;
import cbpg.demo.plugin.common.KeyValueStore;
import cbpg.demo.plugin.common.PropertyKeyGenerator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import cbpg.demo.plugin.progdata.pseudonym.PseudonymService;

@Service
public final class AuthService {
    private final String STORE_KEY = PropertyKeyGenerator.prefix("auth");
    private static final Logger LOG = Logger.getInstance(AuthService.class);

    public static AuthService getInstance() {
        return ApplicationManager.getApplication().getService(AuthService.class);
    }

    public synchronized AuthContext login(String username, char[] password) {
        var authProvider = AuthProvider.getInstance();
        var newContext = authProvider.authenticate(username, password);

        var pseudonymService = PseudonymService.getInstance();
        pseudonymService.generateAndStore(newContext.getLoginName(), password);

        var consentStore = ConsentStore.getInstance();
        var consentContext = newContext.hasConsent()
                ? new ConsentContext(newContext.getLoginName())
                : null;
        consentStore.setContext(consentContext);

        if (consentContext == null) {
            throw new NoConsentException();
        }

        setContext(newContext);
        var messageBus = ApplicationManager.getApplication().getMessageBus();
        var publisher = messageBus.syncPublisher(AuthListener.AUTH_TOPIC);
        publisher.afterLogin(newContext);

        return newContext;
    }

    public synchronized AuthContext requestRefresh(boolean force) {
        var authContext = getContext();
        if (authContext == null) {
            throw new RuntimeException("Not logged in");
        }

        var authProvider = AuthProvider.getInstance();
        var shouldRefresh = force || authProvider.refreshRequired(authContext);
        if (!shouldRefresh) {
            return authContext;
        }

        try {
            var newContext = authProvider.refresh(authContext);

            var consentStore = ConsentStore.getInstance();
            var consentContext = newContext.hasConsent()
                    ? new ConsentContext(newContext.getLoginName())
                    : null;
            consentStore.setContext(consentContext);

            if (consentContext == null) {
                logout();
                throw new NoConsentException();
            }

            setContext(newContext);
            return newContext;
        } catch (SessionExpiredException e) {
            logout();
            throw e;
        }
    }

    public synchronized void logout() {
        setContext(null);
    }

    private void setContext(AuthContext ctx) {
        var storageService = KeyValueStore.getInstance();
        storageService.setValue(STORE_KEY, ctx);
    }

    public AuthContext getContext() {
        var storageService = KeyValueStore.getInstance();
        return storageService.getValue(STORE_KEY, AuthContext.class);
    }
}
