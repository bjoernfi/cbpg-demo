package cbpg.demo.plugin.progdata.event.persistence;

import cbpg.demo.plugin.common.DataDirManager;
import cbpg.demo.plugin.common.JsonService;
import cbpg.demo.plugin.common.LogMessage;
import cbpg.demo.plugin.progdata.event.model.Event;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public final class H2EventStore implements EventStore {

    private static final Logger LOG = Logger.getInstance(H2EventStore.class);

    private ConnectionSource connectionSource = null;

    private synchronized void initConnection() throws SQLException {
        if (connectionSource != null) {
            return;
        }

        var dataDirManager = DataDirManager.getInstance();
        var dbPath = dataDirManager.getOrCreateDataDir()
            .toPath().toAbsolutePath()
            .resolve("events.db");

        // use H2 automatic mixed mode in case two intellij instances are started
        // (to prevent exceptions due to locking)
        var databaseUrl = "jdbc:h2:%s;AUTO_SERVER=TRUE".formatted(dbPath.toString());
        connectionSource = new JdbcPooledConnectionSource(databaseUrl);

        TableUtils.createTableIfNotExists(connectionSource, EventModel.class);
    }

    public long getEventsCount(String loginName) {
        try {
            initConnection();
            Dao<EventModel, String> eventDao = DaoManager.createDao(connectionSource,
                EventModel.class);
            var queryBuilder = eventDao.queryBuilder().where().eq("loginName", loginName);
            return queryBuilder.countOf();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Event> getEvents(long limit, String loginName) {
        try {
            initConnection();
            Dao<EventModel, String> eventDao = DaoManager.createDao(connectionSource,
                EventModel.class);
            var queryBuilder = eventDao.queryBuilder().limit(limit).where()
                .eq("loginName", loginName);
            var models = queryBuilder.query();

            var json = JsonService.getInstance();
            return models.stream().map(m -> json.parse(m.getEvent(), Event.class))
                .collect(Collectors.toList());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteEvents(List<Event> events) {
        try {
            initConnection();
            Dao<EventModel, String> eventDao = DaoManager.createDao(connectionSource,
                EventModel.class);
            var ids = events.stream().map(e -> e.getId().toString()).collect(Collectors.toList());
            eventDao.deleteIds(ids);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void appendEvent(Event event, String loginName) {
        try {
            initConnection();

            var json = JsonService.getInstance();

            var model = new EventModel();
            model.setId(event.getId().toString());
            model.setEvent(json.write(event));
            model.setLoginName(loginName);
            Dao<EventModel, String> eventDao = DaoManager.createDao(connectionSource,
                EventModel.class);
            eventDao.create(model);

            LOG.debug(LogMessage.from("Event stored: %s (%s)".formatted(event.toString(true), loginName)));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
