package cbpg.demo.plugin.auth.login;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import cbpg.demo.plugin.auth.AuthService;
import org.jetbrains.annotations.NotNull;

public class LoginAction extends AnAction implements DumbAware {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var loginController = new LoginController(new LoginModel());
        loginController.requestLogin();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        var authService = AuthService.getInstance();
        var isAuthenticated = authService.getContext() != null;

        event.getPresentation()
            .setEnabledAndVisible(!isAuthenticated);
    }

}
