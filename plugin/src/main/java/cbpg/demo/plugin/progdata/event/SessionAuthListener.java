package cbpg.demo.plugin.progdata.event;

import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.common.config.ConfigService;
import cbpg.demo.plugin.progdata.upload.UploadJobScheduler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import cbpg.demo.plugin.auth.provider.AuthContext;
import cbpg.demo.plugin.auth.AuthListener;

public class SessionAuthListener implements AuthListener {
    private static final Logger LOG = Logger.getInstance(SessionAuthListener.class);

    @Override
    public void afterLogin(AuthContext ctx) {
        var consentStore = ConsentStore.getInstance();
        var consentContext = consentStore.getContext();
        if (consentContext == null) {
            return;
        }

        var projectManager = ProjectManager.getInstance();
        for(var project : projectManager.getOpenProjects()) {
            LOG.debug("afterLogin: project %s".formatted(project.getLocationHash()));
            var sessionManager = project.getService(SessionManager.class);
            sessionManager.startSession();
        }

        var configService = ConfigService.getInstance();
        if (configService.getConfig().data().upload().automated()) {
            var uploadJobScheduler = UploadJobScheduler.getInstance();
            uploadJobScheduler.runUploadJob();
        }
    }
}
