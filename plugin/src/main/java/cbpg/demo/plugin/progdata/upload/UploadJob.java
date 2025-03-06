package cbpg.demo.plugin.progdata.upload;


import cbpg.demo.plugin.auth.AuthService;
import cbpg.demo.plugin.auth.RetryCancelException;
import cbpg.demo.plugin.common.ErrorAdvice;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.util.Consumer;
import com.intellij.util.ExceptionUtil;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;

/**
 * Attempts to upload collected data. If the upload fails, the upload is retried using the given
 * retry policy (interval function). This may run forever if our service is never available.
 */
public class UploadJob extends TimerTask {
    private static final Logger LOG = Logger.getInstance(UploadJob.class);

    private final IntervalFunction intervalFunction;

    public UploadJob(IntervalFunction intervalFunction) {
        this.intervalFunction = intervalFunction;
    }

    public void run() {
        var retryConfig = RetryConfig.custom()
            .maxAttempts(Integer.MAX_VALUE)
            .intervalFunction(intervalFunction)
            .build();
        var retry = Retry.of("upload", retryConfig);
        var r = Retry.decorateRunnable(retry, this::tryUpload);
        r.run();
    }

    /**
     * Attempts to upload. This has to be a blocking call that throws an exception
     * if a retry should be attempted.
     */
    private void tryUpload() {
        LOG.debug("tryUpload");

        try {
            // show the progress in the status bar at the bottom.
            // the progress is only shown if the upload call is pending.
            // progress is not shown while waiting for the retry.
            runWithProgress(
                    "Uploading Data",
                    (indicator) -> {
                        indicator.setIndeterminate(true);

                        var authService = AuthService.getInstance();
                        var isAuthenticated = authService.getContext() != null;
                        if (!isAuthenticated) {
                            LOG.debug("runWithProgress: Not authenticated, skipping upload.");
                            return;
                        }

                        var syncService = UploadService.getInstance();
                        syncService.uploadData();
                    }
            );
        } catch (RetryCancelException e) {
            // we don't want to retry if the user canceled,
            // so we need to catch this exception
            ErrorAdvice.handleError(e);
        }
    }

    private void runWithProgress(String title,
        Consumer<ProgressIndicator> r) {
        // use a future for blocking the caller because ProgressManager.run is running the task in another thread
        var currentExecution = new CompletableFuture<>();

        // because it's asynchronous, we need to propagate exceptions occuring in the spawned thread
        var exception = new AtomicReference<Throwable>();

        // don't show a modal progress dialog. instead show the progress in the status bar at the bottom
        ProgressManager.getInstance()
            .run(new Backgroundable(null, title, false,
                PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        r.consume(indicator);
                    } catch (Throwable t) {
                        exception.set(t);
                    } finally {
                        currentExecution.complete(null);
                    }
                }
            });

        try {
            currentExecution.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var t = exception.get();
        if (t != null) {
            ExceptionUtil.rethrowUnchecked(t);
        }
    }
}
