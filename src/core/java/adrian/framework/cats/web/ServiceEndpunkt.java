package adrian.framework.cats.web;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@ThreadSafe
public class ServiceEndpunkt implements Runnable {

    private final Config config;
    private final State  state;

    private static class State {
        @GuardedBy("ServiceEndpunkt") boolean shouldStop = false;
        @GuardedBy("ServiceEndpunkt") boolean isRunning  = false;
    }

    private record Config(int port) { }

    public ServiceEndpunkt(int port) {
        this.config = new Config(port);
        this.state = new State();
    }

    public synchronized void signalStop() {
        state.shouldStop = true;
    }

    @Override
    public void run() {
        setRunning();
        try (ServerSocket serverSocket = new ServerSocket(config.port)) {
            while (!state.shouldStop) {
                Socket socket = serverSocket.accept();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            setStopped();
        }
    }

    private synchronized void setRunning() {
        if (state.isRunning) return;
        state.isRunning = true;
        state.shouldStop = false;
    }

    private synchronized void setStopped() {
        state.isRunning = false;
    }
}
