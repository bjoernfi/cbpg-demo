package cbpg.demo.plugin.auth;

import cbpg.demo.plugin.auth.consent.NoConsentRetryHandler;
import cbpg.demo.plugin.auth.session.SessionExpiredRetryHandler;
import cbpg.demo.plugin.auth.provider.AuthContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Computable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

@Service
public final class AuthGuard {
    private static final Logger LOG = Logger.getInstance(AuthGuard.class);

    public static AuthGuard getInstance() {
        return ApplicationManager.getApplication().getService(AuthGuard.class);
    }

    public synchronized AuthContext refreshAuthentication() {
        var retryHandlers = List.of(
                new SessionExpiredRetryHandler(),
                new NoConsentRetryHandler()
        );

        return refreshAuthentication(retryHandlers);
    }

    private AuthContext refreshAuthentication(List<RetryHandler> retryHandlers) {
        try {
            LOG.debug("refreshAuthentication");
            var authService = AuthService.getInstance();
            return authService.requestRefresh(true);
        } catch (Exception ex) {
            var handler = retryHandlers.stream().filter(g -> g.canHandle(ex))
                .findFirst()
                .orElse(null);
            if (handler == null) {
                LOG.debug("No retry handler found for exception %s".formatted(ex.getClass().toString()));
                throw ex;
            }

            var shouldRetry = runSynchronouslyOnEDT(handler::shouldRetry);
            LOG.debug("shouldRetry=%s according to handler %s".formatted(shouldRetry, handler.getClass().toString()));
            if (shouldRetry) {
                return refreshAuthentication(retryHandlers);
            }

            throw new RetryCancelException();
        }
    }

    /**
     * execute the given computable on the UI thread and wait for the result.
     */
    private <T> T runSynchronouslyOnEDT(Computable<T> r) {
        // we check for isDispatchThread because there will be a deadlock if invokeLater is executed on UI thread
        if (ApplicationManager.getApplication().isDispatchThread()) {
            LOG.debug("runSynchronouslyOnEDT: EDT");
            return r.compute();
        } else {
            LOG.debug("runSynchronouslyOnEDT: !EDT");
            RunnableFuture<T> rf = new FutureTask<>(() -> r.compute());
            ApplicationManager.getApplication().invokeLater(rf);
            try {
                return rf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
