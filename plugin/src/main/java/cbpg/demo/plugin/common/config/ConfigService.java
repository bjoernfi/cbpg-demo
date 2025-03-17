package cbpg.demo.plugin.common.config;

import com.intellij.openapi.application.ApplicationManager;

public interface ConfigService {

    static ConfigService getInstance() {
        return ApplicationManager.getApplication().getService(ConfigService.class);
    }

    Config getConfig();
}
