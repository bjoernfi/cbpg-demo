package cbpg.demo.plugin.progdata.event;

import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.common.ErrorAdvice;
import cbpg.demo.plugin.progdata.event.persistence.EventStore;
import cbpg.demo.plugin.progdata.event.model.FileSaveEvent;
import cbpg.demo.plugin.progdata.event.model.FileSnapshot;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Records changes to template files. In contrast to {@link com.intellij.openapi.editor.event.DocumentListener},
 * this listener performs on the file system level.
 * <p>
 * This listener will also handle files that are saved externally without using the IDE.
 * As soon as the IDE receives focus, it will run its internal file system change detection and call the listener.
 * See the <a href="https://plugins.jetbrains.com/docs/intellij/virtual-file-system.html">docs about the Virtual File System</a>.
 */
public class PluginFileContentChangeListener implements BulkFileListener {
    private static final Logger LOG = Logger.getInstance(PluginFileContentChangeListener.class);

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        try {
            var consentStore = ConsentStore.getInstance();
            var consentContext = consentStore.getContext();
            if (consentContext == null) {
                return;
            }

            for (var event : events) {
                if (event instanceof VFileContentChangeEvent changeEvent) {
                    LOG.debug("after: content change event");

                    var project = ProjectLocator.getInstance()
                            .guessProjectForFile(changeEvent.getFile());

                    if(project == null) {
                        continue;
                    }

                    LOG.debug("after: project %s".formatted(project.getLocationHash()));
                    var snapshot = createSnapshot(changeEvent.getFile(), project);
                    if(snapshot == null) {
                        continue;
                    }

                    var sessionManager = project.getService(SessionManager.class);
                    var fileSaveEvent = sessionManager.createEvent(FileSaveEvent.class);
                    fileSaveEvent.setSnapshot(snapshot);

                    var eventStore = EventStore.getInstance();
                    eventStore.appendEvent(fileSaveEvent, consentContext.loginName());
                }
            }
        } catch (Exception ex) {
            ErrorAdvice.handleError(ex);
        }
    }

    private FileSnapshot createSnapshot(VirtualFile file, Project project) {
        var path = file.toNioPath();
        if (!path.toFile().exists() || !path.toString().endsWith(".java")) {
            return null;
        }

        var projectPath = project.getBasePath();
        if (projectPath == null) {
            return null;
        }

        var relativeFilePath = Path.of(projectPath).relativize(file.toNioPath());
        try {
            var content = Files.readString(file.toNioPath(), StandardCharsets.UTF_8);
            return new FileSnapshot(relativeFilePath.toString(), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
