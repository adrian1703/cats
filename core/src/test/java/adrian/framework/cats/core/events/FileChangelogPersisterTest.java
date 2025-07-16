package adrian.framework.cats.core.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Queue;

import static adrian.framework.cats.core.events.FileChangelogPersister.FILE_NAME;
import static org.junit.jupiter.api.Assertions.*;

class FileChangelogPersisterTest {
    FileChangelogPersister persister;

    @BeforeEach
    void setUp() {
        persister = new FileChangelogPersister();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            boolean rc = file.delete();
            assertTrue(rc);
        }
    }

    @AfterEach
    void tearDown() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            boolean rc = file.delete();
            assertTrue(rc);
        }
    }


    @Test
    void persist_shouldCreateFileAndWriteEvent() {
        TestChangelogEvent event = new TestChangelogEvent("Test event");

        persister.persist(event);

        File file = new File(FILE_NAME);
        assertTrue(file.exists(), "Changelog file should exist after persisting an event");
        assertTrue(file.length() > 0, "Changelog file should not be empty after persisting");
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

        // Ensure event descriptions match what was persisted
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