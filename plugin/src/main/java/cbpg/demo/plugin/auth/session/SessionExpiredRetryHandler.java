package cbpg.demo.plugin.auth.session;

import cbpg.demo.plugin.auth.RetryHandler;
import cbpg.demo.plugin.auth.login.LoginController;
import cbpg.demo.plugin.auth.login.LoginModel;


/**
 * prompts for login if the session has expired. if the user successfully logins, a retry will be
 * attempted.
 */
public class SessionExpiredRetryHandler implements RetryHandler {

    private boolean retried;

    @Override
    public boolean shouldRetry() {
        if (retried) {
            throw new RuntimeException("Huh? Session expired again?");
        }

        retried = true;
        var model = new LoginModel();
        model.setContextMessage("The session has expired. Please log in again.");

        var loginController = new LoginController(model);
        return loginController.requestLogin();
    }

    @Override
    public boolean canHandle(Exception ex) {
        return ex instanceof SessionExpiredException;
    }
}
