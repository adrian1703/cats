package adrian.framework.cats.core.events;

import java.util.Queue;

public interface EventPersistor<T> {
    void persist(T event);

    Queue<T> readChangelog();
}
