package cbpg.demo.plugin.progdata.event.persistence;

import static com.j256.ormlite.field.DataType.LONG_STRING;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class EventModel {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField(dataType = LONG_STRING)
    private String event;
    @DatabaseField(canBeNull = false)
    private String loginName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "EventModel{" +
            "id='" + id + '\'' +
            ", event='" + event + '\'' +
            ", loginName='" + loginName + '\'' +
            '}';
    }
}
