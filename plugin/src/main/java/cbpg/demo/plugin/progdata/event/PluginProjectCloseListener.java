package cbpg.demo.plugin.progdata.event;

import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.common.ErrorAdvice;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCloseListener;
import org.jetbrains.annotations.NotNull;

/**
 * Records session end event on project close. Note that {@link ProjectCloseListener} is experimental
 * and may be unstable. While we observed that the listener does not miss the project close event,
 * the method (projectClosing) is sometimes called twice with a few milliseconds in between.
 */
public class PluginProjectCloseListener implements ProjectCloseListener {
    private static final Logger LOG = Logger.getInstance(PluginProjectCloseListener.class);

    @Override
    public void projectClosing(@NotNull Project project) {
        try {
            var consentStore = ConsentStore.getInstance();
            var consentContext = consentStore.getContext();
            if (consentContext == null) {
                return;
            }

            LOG.debug("projectClosing: project %s".formatted(project.getLocationHash()));
            var sessionManager = project.getService(SessionManager.class);
            sessionManager.endSession();
        } catch (Exception ex) {
            ErrorAdvice.handleError(ex);
        }
    }
}
