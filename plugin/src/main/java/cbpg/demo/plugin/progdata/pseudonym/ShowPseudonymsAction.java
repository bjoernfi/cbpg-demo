package cbpg.demo.plugin.progdata.pseudonym;

import cbpg.demo.plugin.auth.AuthService;
import cbpg.demo.plugin.common.ErrorAdvice;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class ShowPseudonymsAction extends AnAction implements DumbAware {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            var pseudonymInfoController = new PseudonymInfoController();
            pseudonymInfoController.showPseudonymInfo();
        } catch (Exception ex) {
            ErrorAdvice.handleError(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {

        var authService = AuthService.getInstance();
        var isAuthenticated = authService.getContext() != null;

        event.getPresentation()
            .setEnabled(event.getProject() != null && isAuthenticated);
    }
}
