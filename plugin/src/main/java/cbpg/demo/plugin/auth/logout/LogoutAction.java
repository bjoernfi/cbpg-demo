package cbpg.demo.plugin.auth.logout;

import cbpg.demo.plugin.auth.AuthService;
import cbpg.demo.plugin.common.NotificationService;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class LogoutAction extends AnAction implements DumbAware {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var authService = AuthService.getInstance();
        authService.logout();

        var notificationService = NotificationService.getInstance();
        notificationService.notifyLogout();
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        var authService = AuthService.getInstance();
        var authContext = authService.getContext();
        var isAuthenticated = authContext != null;
        event.getPresentation()
            .setEnabledAndVisible(isAuthenticated);

        if (isAuthenticated) {
            event.getPresentation().setText("Logout (%s)".formatted(authContext.getName()));
        }
    }

}
