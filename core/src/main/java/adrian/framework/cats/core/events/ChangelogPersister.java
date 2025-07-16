package adrian.framework.cats.core.events;

import java.util.Queue;

public interface ChangelogPersister {
    void persist(ChangelogEvent event);

    Queue<ChangelogEvent> readChangelog();
}
