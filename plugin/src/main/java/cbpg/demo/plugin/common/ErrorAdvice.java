package cbpg.demo.plugin.common;

import cbpg.demo.plugin.auth.AuthCommunicationException;
import com.intellij.openapi.diagnostic.Logger;
import cbpg.demo.plugin.auth.RetryCancelException;
import cbpg.demo.plugin.progdata.http.DataCommunicationException;

public class ErrorAdvice {

    private static final Logger LOG = Logger.getInstance(ErrorAdvice.class);

    public static String handleError(Exception e) {
        if (e instanceof AuthCommunicationException) {
            var msg = "Failed to connect to the authentication backend.";
            NotificationService.getInstance().notifyError(msg);
            logResolved(e);
            return msg;
        }

        if (e instanceof RetryCancelException) {
            var msg = "The action was canceled.";
            NotificationService.getInstance().notifyError(msg);
            logResolved(e);
            return msg;
        }

        if (e instanceof DataCommunicationException) {
            var msg = "Failed to connect to the data collection backend.";
            NotificationService.getInstance().notifyError(msg);
            logResolved(e);
            return msg;
        }

        LOG.error(LogMessage.from("Unhandled exception"), e);
        return "Something went wrong.";
    }

    private static void logResolved(Exception e) {
        LOG.warn(LogMessage.from("Resolved exception"), e);
    }
}
