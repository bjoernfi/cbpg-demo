package cbpg.demo.plugin.progdata.upload;

import cbpg.demo.plugin.auth.AuthService;
import cbpg.demo.plugin.common.NotificationService;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import cbpg.demo.plugin.common.ErrorAdvice;
import org.jetbrains.annotations.NotNull;

public class UploadAction extends AnAction implements DumbAware {

    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ProgressManager.getInstance()
            .run(new Task.Backgroundable(e.getProject(), "Uploading Data", false) {
                public void run(ProgressIndicator progressIndicator) {
                    try {
                        var syncService = UploadService.getInstance();
                        var result = syncService.uploadData();
                        var notificationService = NotificationService.getInstance();
                        notificationService.notifyUploadCompleted(result);
                    } catch (Exception ex) {
                        ApplicationManager.getApplication()
                            .invokeLater(() -> ErrorAdvice.handleError(ex));
                    }
                }
            });
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        var authService = AuthService.getInstance();
        var isAuthenticated = authService.getContext() != null;

        event.getPresentation()
            .setEnabled(event.getProject() != null && isAuthenticated);
    }
}
