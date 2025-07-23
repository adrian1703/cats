package adrian.framework.cats.core.events;

import java.util.Queue;

public interface EventPersistor {
    void persist(ChangelogEvent event);

    Queue<ChangelogEvent> readChangelog();
}
