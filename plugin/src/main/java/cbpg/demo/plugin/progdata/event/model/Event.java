package cbpg.demo.plugin.progdata.event.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SessionStartEvent.class, name = "sessionStart"),
    @JsonSubTypes.Type(value = SessionEndEvent.class, name = "sessionEnd"),
    @JsonSubTypes.Type(value = FileSaveEvent.class, name = "fileSave"),
})
public abstract class Event {

    private UUID id;
    private LocalDateTime occuredAt;
    private String pseudonym;
    private UUID sessionId;

    public UUID getId() {
        return id;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOccuredAt(LocalDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event that = (Event) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean compact) {
        return "Event{" +
            "id=" + id +
            ", occuredAt=" + occuredAt +
            ", pseudonym=" + pseudonym +
            ", sessionId=" + sessionId +
            '}';
    }
}
