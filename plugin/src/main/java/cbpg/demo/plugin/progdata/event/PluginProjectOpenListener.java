package cbpg.demo.plugin.progdata.event;

import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.common.ErrorAdvice;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Records session start events as soon as the project is opened the plugin has been initialized.
 * Note that this will not record session starts if the user has not yet logged in once.
 * Thus, the {@link SessionAuthListener} will record the session start as well.
 */
public class PluginProjectOpenListener implements ProjectActivity {
    private static final Logger LOG = Logger.getInstance(PluginProjectOpenListener.class);

    @Nullable
    @Override
    public Object execute(@NotNull Project project,
        @NotNull Continuation<? super Unit> continuation) {

        try {
            var consentStore = ConsentStore.getInstance();
            var consentContext = consentStore.getContext();
            if (consentContext == null) {
                return null;
            }

            LOG.debug("execute: project %s".formatted(project.getLocationHash()));
            var sessionManager = project.getService(SessionManager.class);
            sessionManager.startSession();
        } catch (Exception ex) {
            ErrorAdvice.handleError(ex);
        }

        return null;
    }
}
