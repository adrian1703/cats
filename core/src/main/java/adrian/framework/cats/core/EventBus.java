package adrian.framework.cats.core;

import java.util.Arrays;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventBus {

    private static class State {
        BlockingQueue<Event<?, ?>> events    = new LinkedBlockingQueue<>();
        Queue<EventListener>       listeners = new ConcurrentLinkedQueue<>();
        Thread distributor;
    }
    private final State state;

    public EventBus() {
        this.state = new State();
    }

    void register(EventListener... listener) {
        state.listeners.addAll(Arrays.asList(listener));
    }

    void submit(Event<?, ?>... event) {
        state.events.addAll(Arrays.asList(event));
    }

    void start() {
        state.distributor = new Thread(() -> {
            while (true) {
                try {
                    Event<?, ?> event = state.events.take();
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

    void stop() {
        state.distributor.interrupt();
    }
}
