package cbpg.demo.plugin;


import cbpg.demo.plugin.auth.AuthService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class MainMenuActionGroup extends DefaultActionGroup implements DumbAware {

    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent event) {
        var authService = AuthService.getInstance();
        var isAuthenticated = authService.getContext() != null;

        if (isAuthenticated) {
            event.getPresentation().setIcon(AllIcons.Debugger.Db_no_suspend_method_breakpoint);
        } else {
            event.getPresentation().setIcon(AllIcons.Debugger.Db_muted_method_breakpoint);
        }
    }
}