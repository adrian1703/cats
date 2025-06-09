package adrian.framework.cats.web;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;


@ThreadSafe
public class SocketEndpunkt {

    private final Strategy strategy;
    private final State    state;

    private static class State {
        private final static int           CREATED     = 0;
        private final static int           IS_RUNNING  = 1;
        private final static int           SHOULD_STOP = 2;
        private final static int           IS_STOPPED  = 3;
        private final        AtomicInteger value       = new AtomicInteger(CREATED);

        @GuardedBy("this") private Thread       worker;
        @GuardedBy("this") private ServerSocket serverSocket;
    }

    private record Strategy(int port, TaskFactory socketProcessor) {}

    public SocketEndpunkt(int port, TaskFactory socketProcessor) {
        this.strategy = new Strategy(port, socketProcessor);
        this.state    = new State();
    }

    public int getState() {return state.value.get();}

    public synchronized void signalStop() throws IOException {
        if (!state.value.compareAndSet(State.IS_RUNNING, State.SHOULD_STOP)) return;
        if (state.serverSocket != null && !state.serverSocket.isClosed()) {
            state.serverSocket.close();
        }
        state.worker.interrupt(); // race condition when startListener -> signalStop -> worker thread run()
    }

    public synchronized void startListener() {
        if (!setRunning()) return;
        state.worker = new Thread(() -> {
            try (ServerSocket localSocket = new ServerSocket(strategy.port)) {
                synchronized (this) {
                    state.serverSocket = localSocket;
                }
                while (state.worker.isInterrupted() == false) {
                    Socket   socket;
                    Runnable task;

                    socket = state.serverSocket.accept();
                    task   = strategy.socketProcessor.createTask(socket);
                    task.run();
                }
            } catch (IOException e) {
                if (state.serverSocket != null && state.serverSocket.isClosed()) {
                    // clean shutdown - do nothing
                } else {
                    throw new RuntimeException(e);
                }
            } finally {
                setStopped();
            }
        });
        state.worker.start();
    }

    private synchronized boolean setRunning() {
        boolean isStarting = state.value.compareAndSet(State.CREATED, State.IS_RUNNING) || state.value.compareAndSet(State.IS_STOPPED, State.IS_RUNNING);
        return isStarting;
    }

    private synchronized void setStopped() {
        state.value.compareAndSet(State.IS_RUNNING, State.IS_STOPPED);
    }
}
