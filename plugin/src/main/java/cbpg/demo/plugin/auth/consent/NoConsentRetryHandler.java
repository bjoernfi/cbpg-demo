package cbpg.demo.plugin.auth.consent;

import cbpg.demo.plugin.auth.RetryHandler;
import com.intellij.openapi.ui.Messages;


/**
 * prompts for login if no consent. if the user successfully logins, a retry will be attempted.
 */
public class NoConsentRetryHandler implements RetryHandler {

    @Override
    public boolean shouldRetry() {
        Messages.showMessageDialog(
            "The consent for data collection appears to be withdrawn.\n\nYou may need to log in again after giving consent.",
            "Consent Missing", Messages.getErrorIcon());
        return false;
    }

    @Override
    public boolean canHandle(Exception ex) {
        return ex instanceof NoConsentException;
    }
}
