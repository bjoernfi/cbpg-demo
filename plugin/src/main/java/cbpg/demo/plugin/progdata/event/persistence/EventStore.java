package cbpg.demo.plugin.progdata.event.persistence;

import com.intellij.openapi.application.ApplicationManager;
import cbpg.demo.plugin.progdata.event.model.Event;
import java.util.List;

public interface EventStore {

    static EventStore getInstance() {
        return ApplicationManager.getApplication().getService(EventStore.class);
    }

    long getEventsCount(String loginName);

    List<Event> getEvents(long limit, String loginName);

    void deleteEvents(List<Event> events);

    void appendEvent(Event event, String loginName);
}
