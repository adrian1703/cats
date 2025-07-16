package adrian.framework.cats.core.events;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread-safe event distribution system that manages event publishing and subscription.
 * <p>
 * The EventBus provides a mechanism to decouple event producers from event consumers
 * by implementing a publish-subscribe pattern. Events are queued and distributed
 * to registered listeners in a separate thread.
 * <p>
 * Key features:
 * <ul>
 *   <li>Thread-safe event submission and listener registration
 *   <li>Asynchronous event distribution via dedicated thread
 *   <li>Support for multiple listeners and event types
 *   <li>Controlled lifecycle with start/stop capabilities
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 * EventBus bus = new EventBus();
 * bus.register(new MyEventListener());
 * bus.submit(new MyEvent());
 * bus.start();
 * // ... later
 * bus.stop();
 * </pre>
 *
 * @see EventListener
 * @see Event
 */
@ThreadSafe
public class EventBus {

    private final State  state;
    private final Helper helper;

    private static class State {
        private final BlockingQueue<Event> events    = new LinkedBlockingQueue<>();
        private final Queue<EventListener> listeners = new ConcurrentLinkedQueue<>();
        private final AtomicBoolean        isRunning = new AtomicBoolean(false);

        @GuardedBy("this") private Thread distributor;
    }

    private record Helper(ChangelogPersister changelogPersister) {}

    public EventBus(ChangelogPersister changelogPersister) {
        this.state  = new State();
        this.helper = new Helper(changelogPersister);
    }

    /**
     * Starts the event distribution thread that processes events from the queue
     * and delivers them to registered listeners. The distributor thread runs
     * continuously in the background, taking events from the queue and
     * processing them sequentially.
     * <p>
     * There is never more than 1 thread running. Subsequent calls are
     * ignored.
     * <p>
     * The distributor thread will continue running until explicitly stopped
     * using the {@link #stop()} method or when the thread is interrupted.
     */
    public synchronized void start() {
        if (state.isRunning.getAndSet(true)) return;
        state.distributor = new Thread(() -> {
            while (true) {
                try {
                    Event event = state.events.take();
                    for (EventListener listener : state.listeners) {
                        listener.onEvent(event);
                    }
                } catch (InterruptedException e) {
                    return; // exit
                }
            }
        });
        state.distributor.start();
    }

    /**
     * Interrupts the distributor thread and sets the
     * `isRunning` flag to false allowing for graceful exit
     * There might be a slight desync to when the thread actually
     * stops.
     */
    public synchronized void stop() {
        if (state.distributor == null) return;
        state.isRunning.set(false);
        state.distributor.interrupt();
    }

    /**
     * Return the current intended state for the event distributor
     * thread.
     *
     * @return true if the thread started; false if interrupt was sent.
     */
    public boolean distributorIsRunning() {
        // return state.distributor.isAlive();
        return state.isRunning.get(); // should be more clear than calling alive
    }

    void register(EventListener... listener) {
        state.listeners.addAll(Arrays.asList(listener));
    }

    void submit(Event... event) {
        Arrays.stream(event)
              .filter(e -> e instanceof ChangelogEvent)
              .forEach(e -> helper.changelogPersister.persist((ChangelogEvent) e));
        state.events.addAll(Arrays.asList(event));
    }
}