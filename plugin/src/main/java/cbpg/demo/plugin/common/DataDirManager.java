package cbpg.demo.plugin.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import cbpg.demo.plugin.common.config.ConfigService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public final class DataDirManager {

    public static DataDirManager getInstance() {
        return ApplicationManager.getApplication().getService(DataDirManager.class);
    }

    public synchronized File getOrCreateDataDir() {
        var configService = ConfigService.getInstance();
        var dataDir = configService.getConfig().dataDir();
        var resolvedDataDirPath = Path.of(
            dataDir.replace("${user.home}", System.getProperty("user.home")));

        try {
            if (!Files.exists(resolvedDataDirPath)) {
                Files.createDirectories(resolvedDataDirPath);
            }

            return resolvedDataDirPath.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
