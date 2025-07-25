package adrian.framework.cats.core.events;

import org.junit.jupiter.api.Test;

import java.io.File;

import static adrian.framework.cats.core.events.FileEventPersistor.FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileEventPersistorTest extends EventPersistorTest {

    @Override
    protected EventPersistor createPersister() {
        return new FileEventPersistor();
    }

    @Override
    protected void cleanUp() {
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
}