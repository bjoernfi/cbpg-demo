package cbpg.demo.plugin.auth.consent;

import cbpg.demo.plugin.auth.provider.AuthContext;
import cbpg.demo.plugin.common.KeyValueStore;
import cbpg.demo.plugin.common.PropertyKeyGenerator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

/**
 * The consent is stored separately to enable offline data collection while being logged out.
 * Listeners collecting data should check the consent through this service and NOT through
 * {@link AuthContext#hasConsent()} because no context exists if logged out.
 */
@Service
public final class ConsentStore {

    private final String KEY = PropertyKeyGenerator.prefix("consent");

    public static ConsentStore getInstance() {
        return ApplicationManager.getApplication().getService(ConsentStore.class);
    }

    public void setContext(ConsentContext context) {
        var storageService = KeyValueStore.getInstance();
        storageService.setValue(KEY, context);
    }

    public ConsentContext getContext() {
        var storageService = KeyValueStore.getInstance();
        return storageService.getValue(KEY, ConsentContext.class);
    }
}
