package cbpg.demo.backend;


import java.util.List;
import java.util.Map;

public class UploadBatch {
    private List<Map> events;
    private List<String> pseudonyms;

    public List<Map> getEvents() {
        return events;
    }

    public void setEvents(List<Map> events) {
        this.events = events;
    }

    public List<String> getPseudonyms() {
        return pseudonyms;
    }

    public void setPseudonyms(List<String> pseudonyms) {
        this.pseudonyms = pseudonyms;
    }
}
