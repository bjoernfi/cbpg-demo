package cbpg.demo.plugin.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Drop-in replacement for {@link com.intellij.ide.util.PropertiesComponent} for persisting data.
 * We don't use {@link com.intellij.ide.util.PropertiesComponent}, since there are some
 * <a href="https://youtrack.jetbrains.com/issue/IJPL-13234/Lost-settings-after-update-to-2022.3">bug reports</a>
 * indicating that this data can get lost during IDE updates.
 */
@Service
public final class KeyValueStore {

    private Properties properties;

    public static KeyValueStore getInstance() {
        return ApplicationManager.getApplication().getService(KeyValueStore.class);
    }

    public synchronized <T> T getValue(String key, Class<T> clazz) {
        loadProperties();
        var raw = properties.getProperty(key);
        var json = JsonService.getInstance();
        return raw != null ? json.parse(raw, clazz) : null;
    }

    public synchronized <T> void setValue(String key, T value) {
        loadProperties();
        if (value == null) {
            properties.remove(key);
        } else {
            var json = JsonService.getInstance();
            properties.setProperty(key, json.write(value));
        }
        saveProperties();
    }

    private File getPropertiesFile() {
        var dataDirManager = DataDirManager.getInstance();
        var dataDirectory = dataDirManager.getOrCreateDataDir();
        return dataDirectory.toPath().resolve("store.properties").toFile();
    }

    private void loadProperties() {
        if (properties != null) {
            return;
        }

        var properties = new Properties();
        var propertiesFile = getPropertiesFile();
        if (!propertiesFile.exists()) {
            this.properties = properties;
            return;
        }

        try (var inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
            this.properties = properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveProperties() {
        try (var outputStream = new FileOutputStream(getPropertiesFile())) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
