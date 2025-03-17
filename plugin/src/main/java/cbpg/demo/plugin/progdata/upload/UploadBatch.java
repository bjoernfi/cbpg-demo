package cbpg.demo.plugin.progdata.upload;

import cbpg.demo.plugin.progdata.event.model.Event;

import java.util.List;

public class UploadBatch {

    private List<Event> events;
    private List<String> pseudonyms;

    public UploadBatch(List<Event> events, List<String> pseudonyms) {
        this.events = events;
        this.pseudonyms = pseudonyms;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<String> getPseudonyms() {
        return pseudonyms;
    }

    public void setPseudonyms(List<String> pseudonyms) {
        this.pseudonyms = pseudonyms;
    }
}
