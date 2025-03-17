package cbpg.demo.plugin.progdata;

import cbpg.demo.plugin.common.ErrorAdvice;
import cbpg.demo.plugin.progdata.upload.UploadJobScheduler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import cbpg.demo.plugin.common.PluginDisposable;
import cbpg.demo.plugin.common.config.ConfigService;
import cbpg.demo.plugin.progdata.event.PluginFileContentChangeListener;

import java.util.List;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InitializeDataCollectionActivity implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project,
        @NotNull Continuation<? super Unit> continuation) {

        try {
            var configService = ConfigService.getInstance();

            var pluginDisposable = PluginDisposable.getInstance(project);
            var messageBus = project.getMessageBus();
            var messageBusConnection = messageBus.connect(pluginDisposable);
            var vfsListeners = List.of(
                new PluginFileContentChangeListener()
            );

            for(var vfsListener : vfsListeners) {
                messageBusConnection
                    .subscribe(VirtualFileManager.VFS_CHANGES, vfsListener);
            }

            if (configService.getConfig().data().upload().automated()) {
                var uploadJobScheduler = UploadJobScheduler.getInstance();
                uploadJobScheduler.scheduleUploadJob();
            }
        } catch (Exception ex) {
            ErrorAdvice.handleError(ex);
        }

        return null;
    }
}
