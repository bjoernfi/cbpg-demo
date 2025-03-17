package cbpg.demo.plugin.auth.login;

import cbpg.demo.plugin.auth.consent.NoConsentException;
import cbpg.demo.plugin.common.NotificationService;
import cbpg.demo.plugin.common.ErrorAdvice;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import cbpg.demo.plugin.auth.AuthService;
import cbpg.demo.plugin.auth.InvalidCredentialsException;

public class LoginController {

    private LoginModel model;
    private LoginDialog dialog;

    public LoginController(LoginModel model) {
        this.model = model;
    }

    public boolean requestLogin() {
        dialog = new LoginDialog(model);
        dialog.addListener(new LoginListener() {
            @Override
            public void loginRequested() {
                performLogin();
            }

            @Override
            public void usernameChanged() {
                model.setLastRemoteError(null);
                dialog.setLoginModel(model);
            }

            @Override
            public void passwordChanged() {
                model.setLastRemoteError(null);
                dialog.setLoginModel(model);
            }
        });

        return dialog.showAndGet();
    }


    public void performLogin() {
        model.setLoggingIn(true);
        dialog.setLoginModel(model);

        var username = dialog.getUsernameInput().getText();
        var password = dialog.getPasswordInput().getPassword();

        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Logging in", false) {
            public void run(ProgressIndicator progressIndicator) {
                var authService = AuthService.getInstance();

                try {
                    var context = authService.login(username, password);
                    ApplicationManager.getApplication().invokeLater(() -> dialog.close(0));

                    var notificationService = NotificationService.getInstance();
                    notificationService.notifyLogin(context);
                } catch (InvalidCredentialsException ex) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        model.setLastRemoteError("Invalid credentials");
                        model.setLoggingIn(false);
                        dialog.setLoginModel(model);
                    });
                } catch (NoConsentException ex) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        model.setLastRemoteError(
                                "You need to give your consent to data collection before you can use this plugin.");
                        model.setLoggingIn(false);
                        dialog.setLoginModel(model);
                    });
                } catch (Exception ex) {
                    var message = ErrorAdvice.handleError(ex);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        model.setLastRemoteError(message);
                        model.setLoggingIn(false);
                        dialog.setLoginModel(model);
                    });
                }
            }
        });
    }
}
