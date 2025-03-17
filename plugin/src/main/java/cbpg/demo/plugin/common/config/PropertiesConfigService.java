package cbpg.demo.plugin.common.config;

import java.util.Properties;

public class PropertiesConfigService implements ConfigService {

    private Config config;

    @Override
    public Config getConfig() {
        if (config != null) {
            return config;
        }

        var properties = getPropertiesFile();

        var dataDir = properties.getProperty("data.dir");
        var dataConfig = parseProgDataConfig(properties);
        var keycloakConfig = parseKeycloakConfig(properties);

        config = new Config(
            dataDir,
            keycloakConfig,
            dataConfig
        );

        return config;
    }

    private Properties getPropertiesFile() {
        var application = readProperties("application.properties");
        var override = readProperties("application.properties.override");

        var merged = new Properties();
        merged.putAll(application);
        merged.putAll(override);
        return merged;
    }

    private Properties readProperties(String path) {
        var properties = new Properties();
        var fis = getClass().getResourceAsStream("/" + path);

        if (fis == null) {
            return properties;
        }

        try (fis) {
            properties.load(fis);
            return properties;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Config.KeycloakConfig parseKeycloakConfig(Properties properties) {
        var authServerUrl = properties.getProperty("keycloak.url");
        var clientId = properties.getProperty("keycloak.clientId");
        var realm = properties.getProperty("keycloak.realm");
        var clientSecret = properties.getProperty("keycloak.clientSecret");
        var timeout = Integer.parseInt(properties.getProperty("keycloak.timeout"));

        return new Config.KeycloakConfig(
            authServerUrl,
            realm,
            clientId,
            clientSecret,
            timeout
        );
    }

    private Config.ProgDataConfig parseProgDataConfig(Properties properties) {
        var restBaseUrl = properties.getProperty("progdata.backendUrl");
        var timeout = Integer.parseInt(properties.getProperty("progdata.timeout"));

        return new Config.ProgDataConfig(
            restBaseUrl,
            parseDataUploadConfig(properties),
            timeout
        );
    }

    private Config.ProgDataConfig.UploadConfig parseDataUploadConfig(Properties properties) {
        var initialDelay = Long.parseLong(properties.getProperty("progdata.upload.initialDelay"));
        var period = Long.parseLong(properties.getProperty("progdata.upload.period"));
        var errorInterval = Long.parseLong(properties.getProperty("progdata.upload.errorInterval"));
        var errorMultiplier = Double.parseDouble(
            properties.getProperty("progdata.upload.errorMultiplier"));
        var automated = Boolean.parseBoolean(properties.getProperty("progdata.upload.automated"));

        return new Config.ProgDataConfig.UploadConfig(
            initialDelay,
            period,
            errorInterval,
            errorMultiplier,
            automated
        );
    }
}
