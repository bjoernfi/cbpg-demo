package cbpg.demo.plugin.progdata.event;

import cbpg.demo.plugin.auth.consent.ConsentStore;
import cbpg.demo.plugin.progdata.event.model.Event;
import cbpg.demo.plugin.progdata.event.persistence.EventStore;
import cbpg.demo.plugin.progdata.pseudonym.PseudonymService;
import com.intellij.openapi.components.Service;
import cbpg.demo.plugin.progdata.event.model.SessionEndEvent;
import cbpg.demo.plugin.progdata.event.model.SessionStartEvent;
import com.intellij.openapi.project.Project;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Maintains the session state. A session is defined as the period of time during which the user is working
 * in a project. If another project is opened while the first one is already open, there will be two sessions
 * with different IDs. Thus, this service operates on the project level to maintain a separate session id per project.
 * <p>
 * Moreover, the service is idempotent to prevent duplicate logging of session start/end events
 * (see {@link PluginProjectCloseListener}, {@link PluginProjectOpenListener}, {@link SessionAuthListener}).
 */
@Service(Service.Level.PROJECT)
public final class SessionManager {
    private UUID id;
    private Project project;

    public SessionManager(Project project) {
        this.project = project;
    }

    public synchronized void startSession() {
        if(id != null) {
            // session already started
            return;
        }

        id = UUID.randomUUID();

        var sessionStartEvent = createEvent(SessionStartEvent.class);
        sessionStartEvent.setProjectId(project.getLocationHash());

        var consentStore = ConsentStore.getInstance();
        var consentContext = consentStore.getContext();

        var eventStore = EventStore.getInstance();
        eventStore.appendEvent(sessionStartEvent, consentContext.loginName());
    }

    public synchronized void endSession() {
        if(id == null) {
            // session already ended
            return;
        }

        var sessionEndEvent = createEvent(SessionEndEvent.class);

        var consentStore = ConsentStore.getInstance();
        var consentContext = consentStore.getContext();

        var eventStore = EventStore.getInstance();
        eventStore.appendEvent(sessionEndEvent, consentContext.loginName());

        id = null;
    }

    public <T extends Event> T createEvent(Class<T> clazz) {
        try {
            var event = clazz.getConstructor().newInstance();
            event.setId(UUID.randomUUID());
            event.setOccuredAt(LocalDateTime.now(ZoneOffset.UTC));
            event.setSessionId(id);

            var consentStore = ConsentStore.getInstance();
            var consentContext = consentStore.getContext();
            var pseudonymService = PseudonymService.getInstance();
            var pseudonymContext = pseudonymService.getPseudonymContext(consentContext.loginName());
            event.setPseudonym(pseudonymContext.getNewestPseudonym());

            return event;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
