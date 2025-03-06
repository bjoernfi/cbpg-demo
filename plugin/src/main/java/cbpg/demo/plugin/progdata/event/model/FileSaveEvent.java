package cbpg.demo.plugin.progdata.event.model;

public class FileSaveEvent extends Event {

    private FileSnapshot snapshot;

    public FileSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(FileSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public String toString(boolean compact) {
        return "FileSaveEvent{" +
            ", snapshot=" + (compact ? snapshot != null ? "'...'" : "null" : "'" + snapshot + "'") +
            "} extends " + super.toString(compact);
    }
}
