package cbpg.demo.plugin.progdata.upload;

import cbpg.demo.plugin.common.config.Config;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import cbpg.demo.plugin.common.ErrorAdvice;
import cbpg.demo.plugin.common.config.Config.ProgDataConfig.UploadConfig;
import cbpg.demo.plugin.common.config.ConfigService;
import io.github.resilience4j.core.IntervalFunction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Configures a scheduler for repeated execution of the upload job. The scheduler ensures that
 * upload jobs are not executed in parallel, meaning that if the job is still running (due to the
 * retry policy), no second job is started.
 * <p>
 * Exponential backoff is used as retry policy:
 * - If an error occurs while uploading, the delay is increased exponentially
 * - If the upload is successful, the delay will be reset.
 * - The delay is capped.
 * <p>
 * See {@link Config.ProgDataConfig.UploadConfig} for
 * configuration details.
 * <p>
 * Adapted from:
 * https://stackoverflow.com/questions/11944688/schedule-a-single-threaded-repeating-runnable-in-java-but-skip-the-current-run
 */
@Service
public final class UploadJobScheduler implements Disposable {
    private static final Logger LOG = Logger.getInstance(UploadJobScheduler.class);

    private ScheduledFuture<?> uploadSchedule;
    private Future<?> lastExecution;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static UploadJobScheduler getInstance() {
        return ApplicationManager.getApplication().getService(UploadJobScheduler.class);
    }

    public synchronized void scheduleUploadJob() {
        if(uploadSchedule != null) {
            return;
        }

        var configService = ConfigService.getInstance();
        var upload = configService.getConfig().data().upload();

        uploadSchedule = scheduledExecutor.scheduleAtFixedRate(() -> {
                try {
                    LOG.debug("scheduleAtFixedRate");

                    var projectManager = ProjectManager.getInstance();
                    var anyProjectOpen = projectManager.getOpenProjects().length != 0;

                    LOG.debug("anyProjectOpen: %s".formatted(anyProjectOpen));
                    if(!anyProjectOpen) {
                        dispose();
                        return;
                    }

                    var uploadJob = createUploadJob(upload);
                    submitJob(uploadJob);
                } catch (Exception ex) {
                    ApplicationManager.getApplication().invokeLater(() -> ErrorAdvice.handleError(ex));
                }
            },
            upload.initialDelay(), upload.period(), TimeUnit.SECONDS);
        LOG.debug("uploadSchedule initialized");
    }

    public void runUploadJob() {
        LOG.debug("runUploadJob");
        var configService = ConfigService.getInstance();
        var upload = configService.getConfig().data().upload();

        var uploadJob = createUploadJob(upload);
        submitJob(uploadJob);
    }

    private UploadJob createUploadJob(UploadConfig upload) {
        return new UploadJob(IntervalFunction.ofExponentialBackoff(upload.errorInterval() * 1000,
                upload.errorMultiplier(),
                upload.period() * 1000));
    }

    private synchronized void submitJob(UploadJob uploadJob) {
        if (lastExecution != null && !lastExecution.isDone()) {
            LOG.debug("submitJob: Upload pending, upload job will not be submitted");
            return;
        }

        lastExecution = executor.submit(uploadJob);
        LOG.debug("submitJob: Upload job submitted for execution");
    }

    @Override
    public synchronized void dispose() {
        LOG.debug("dispose");

        if(uploadSchedule != null) {
            uploadSchedule.cancel(true);
            uploadSchedule = null;
        }

        if(lastExecution != null) {
            lastExecution.cancel(true);
            lastExecution = null;
        }
    }
}
