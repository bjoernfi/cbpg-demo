package cbpg.demo.plugin.progdata.event.model;

public class SessionStartEvent extends Event {
    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString(boolean compact) {
        return "SessionStartEvent{" +
            "projectId='" + projectId + '\''
            + "} extends " + super.toString(compact);
    }
}
