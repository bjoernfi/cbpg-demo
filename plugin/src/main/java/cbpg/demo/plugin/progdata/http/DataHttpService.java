package cbpg.demo.plugin.progdata.http;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import cbpg.demo.plugin.common.config.ConfigService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Service
public final class DataHttpService {

    public static DataHttpService getInstance() {
        return ApplicationManager.getApplication().getService(DataHttpService.class);
    }

    public URIBuilder url(String url) {
        try {
            var configService = ConfigService.getInstance();
            return new URIBuilder(configService.getConfig().data().backendUrl()).setPath(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpPost post(URIBuilder uriBuilder) {
        try {
            return new HttpPost(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String responseAsString(HttpUriRequest request) {

        try {
            var configService = ConfigService.getInstance();

            var timeout = configService.getConfig().data().timeout();
            var requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

            try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig).build();
                CloseableHttpResponse response = client.execute(request)) {

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode > 299) {
                    var url = request.getURI().toString();
                    throw new DataResponseException(url, statusCode,
                        response.getStatusLine().getReasonPhrase());
                }

                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }

        } catch (IOException e) {
            throw new DataCommunicationException(e);
        }
    }
}
