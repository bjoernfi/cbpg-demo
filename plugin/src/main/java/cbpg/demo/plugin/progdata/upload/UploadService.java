package cbpg.demo.plugin.progdata.upload;


import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.auth.AuthGuard;
import cbpg.demo.plugin.common.JsonService;
import cbpg.demo.plugin.common.LogMessage;
import cbpg.demo.plugin.progdata.http.DataHttpService;
import cbpg.demo.plugin.progdata.event.persistence.EventStore;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import cbpg.demo.plugin.progdata.pseudonym.PseudonymService;
import java.util.function.Consumer;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

@Service
public final class UploadService {

    private static final Logger LOG = Logger.getInstance(UploadService.class);

    public static UploadService getInstance() {
        return ApplicationManager.getApplication().getService(UploadService.class);
    }

    public UploadResult uploadData() {
        // check if auth still valid (and pseudonyms up to date) before uploading
        // this is a guaranteed online check, so that remote updates (due to consent or password changes)
        // are reflected immediately. If the consent was revoked or password was changed,
        // this will throw a corresponding exception which prevents the upload.
        var authGuard = AuthGuard.getInstance();
        authGuard.refreshAuthentication();

        var consentStore = ConsentStore.getInstance();
        var consentContext = consentStore.getContext();

        var pseudonymService = PseudonymService.getInstance();
        var pseudonymContext = pseudonymService.getPseudonymContext(consentContext.loginName());

        var eventStore = EventStore.getInstance();
        var totalCount = eventStore.getEventsCount(consentContext.loginName());
        LOG.info(LogMessage.from("Syncing %s pending events...".formatted(eventStore.getEventsCount(consentContext.loginName()))));

        var batchSize = 5;
        var batches = (long) Math.ceil((double) totalCount / batchSize);

        for (int i = 0; i < batches; i++) {
            var events = eventStore.getEvents(batchSize, consentContext.loginName());

            var batch = new UploadBatch(
                events,
                pseudonymContext.getPseudonyms()
            );

            uploadBatch(batch);
            eventStore.deleteEvents(events);

            var current = i * batchSize + events.size();
            LOG.debug(LogMessage.from("Uploaded %s/%s events".formatted(current, totalCount)));
        }

        return new UploadResult(totalCount);
    }

    private void uploadBatch(UploadBatch batch) {
        var json = JsonService.getInstance();

        var backend = DataHttpService.getInstance();
        var url = backend.url("/upload");
        var httpRequest = backend.post(url);
        httpRequest.setEntity(new StringEntity(json.write(batch), ContentType.APPLICATION_JSON));

        backend.responseAsString(httpRequest);
    }
}
