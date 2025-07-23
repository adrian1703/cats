package adrian.framework.cats.core.events;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of {@link EventPersistor} that persists changelog events
 * by serializing them to a file and reads them back by deserializing.
 * <p>
 * This implementation saves events to a file named "changelog.dat" in the working directory.
 * Events must be {@link Serializable} (as ChangelogEvent implements Serializable).
 */
public class FileEventPersistor implements EventPersistor<ChangelogEvent> {

    public static final String FILE_NAME = "changelog.dat";

    /**
     * Extension of {@link ObjectOutputStream} to avoid writing a header when appending objects
     * to an existing file, which is required to append objects multiple times.
     */
    private static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            File file = new File(FILE_NAME);
            if (file.length() == 0) {
                super.writeStreamHeader(); // write header only if file is empty
            } else {
                reset(); // do not write a header when appending
            }
        }
    }

    /**
     * Persists the given changelog event by appending it to the changelog file.
     * Each event is serialized and appended, allowing later deserialization.
     *
     * @param event the {@link ChangelogEvent} to persist; must not be null
     */
    @Override
    public synchronized void persist(ChangelogEvent event) {
        assert event != null;

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME, true);
             AppendingObjectOutputStream oos = new AppendingObjectOutputStream(fos)) {
            oos.writeObject(event);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist changelog event", e);
        }
    }

    /**
     * Reads the changelog file and deserializes all stored events into a queue.
     * If the file does not exist or an error occurs, returns an empty queue.
     *
     * @return a {@link Queue} containing all deserialized {@link ChangelogEvent} instances
     */
    @Override
    public synchronized Queue<ChangelogEvent> readChangelog() {
        Queue<ChangelogEvent> events = new LinkedList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return events;
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    assert obj instanceof ChangelogEvent;
                    events.add((ChangelogEvent) obj);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read changelog", e);
        }

        return events;
    }
}
