package cbpg.demo.plugin.progdata.event.model;

public class SessionEndEvent extends Event {

    @Override
    public String toString(boolean compact) {
        return "SessionEndEvent{} extends " + super.toString(compact);
    }
}
