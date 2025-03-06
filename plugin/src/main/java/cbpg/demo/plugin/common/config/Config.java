package cbpg.demo.plugin.common.config;

public record Config(String dataDir,
                     KeycloakConfig keycloak,
                     ProgDataConfig data
) {

    public record KeycloakConfig(String url, String realm, String clientId, String clientSecret,
                                 int timeout) {

    }

    public record ProgDataConfig(String backendUrl, UploadConfig upload, int timeout) {

        /**
         * @param initialDelay    delay (in seconds) when the first upload attempt will occur after
         *                        the scheduling has been initiated
         * @param period          delay (in seconds) between upload attempts
         * @param errorInterval   base delay (in seconds) used for exponential backoff algorithm
         * @param errorMultiplier used for exponential backoff algorithm
         */
        public record UploadConfig(long initialDelay, long period, long errorInterval,
                                   double errorMultiplier, boolean automated) {

        }
    }

}
