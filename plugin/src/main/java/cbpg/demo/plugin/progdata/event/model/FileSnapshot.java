package cbpg.demo.plugin.progdata.event.model;

public class FileSnapshot {

    private String path;
    private String content;

    public FileSnapshot() {
    }

    public FileSnapshot(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
