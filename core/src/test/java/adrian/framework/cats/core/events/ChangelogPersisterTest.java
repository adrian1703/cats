package adrian.framework.cats.core.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

abstract class ChangelogPersisterTest {

    protected ChangelogPersister persister;

    /**
     * Creates the instance of ChangelogPersister to be tested.
     * Subclasses must provide their own implementation.
     */
    protected abstract ChangelogPersister createPersister();

    @BeforeEach
    void setUp() {
        persister = createPersister();
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    /**
     * Subclasses override if needed to clean up resources between runs.
     */
    protected void cleanUp() {
        // default is no-op
    }

    @Test
    void readChangelog_shouldReturnEmptyQueueIfNoFile() {
        Queue<ChangelogEvent> events = persister.readChangelog();

        assertNotNull(events, "Returned queue should never be null");
        assertTrue(events.isEmpty(), "Queue should be empty if no changelog file exists");
    }

    @Test
    void readChangelog_shouldReturnPersistedEvents() {
        TestChangelogEvent event1 = new TestChangelogEvent("First event");
        TestChangelogEvent event2 = new TestChangelogEvent("Second event");

        persister.persist(event1);
        persister.persist(event2);

        Queue<ChangelogEvent> events = persister.readChangelog();

        assertNotNull(events);
        assertEquals(2, events.size(), "Queue should contain both persisted events");

        assertTrue(events.stream()
                         .anyMatch(e -> "First event".equals(e.getDescription())), "Queue should contain the first event");
        assertTrue(events.stream()
                         .anyMatch(e -> "Second event".equals(e.getDescription())), "Queue should contain the second event");
    }

    @Test
    void persist_shouldAppendMultipleEventsCorrectly() {
        for (int i = 1; i <= 5; i++) {
            TestChangelogEvent event = new TestChangelogEvent("Event " + i);
            persister.persist(event);
        }

        Queue<ChangelogEvent> events = persister.readChangelog();
        assertEquals(5, events.size(), "All persisted events should be read back");
    }
}
