package cbpg.demo.backend;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "events")
public class EventRecordModel {
    @Id
    private String id;
    private String userId;
    private Map event;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map getEvent() {
        return event;
    }

    public void setEvent(Map event) {
        this.event = event;
    }
}
