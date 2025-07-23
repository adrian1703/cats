package adrian.framework.cats.core.events;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryEventPersistor implements EventPersistor<ChangelogEvent> {

    private final Queue<ChangelogEvent> events = new ConcurrentLinkedQueue<>();

    @Override
    public void persist(ChangelogEvent event) {
        events.add(event);
    }

    @Override
    public Queue<ChangelogEvent> readChangelog() {
        // Return a snapshot copy to avoid external modification
        return new ConcurrentLinkedQueue<>(events);
    }
}
