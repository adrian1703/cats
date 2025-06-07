package adrian.framework.cats.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceEndpunkt implements Runnable {

    private static class State {
        volatile boolean shouldStop = false;
        volatile boolean isRunning  = false;
    }

    private static class Config {
        protected int port;
    }
    private final Config config;
    private final State  state;

    public ServiceEndpunkt(int port) {
        this.config = new Config();
        this.config.port = port;
        this.state = new State();
    }

    private synchronized void setRunning() {
        if (state.isRunning) return;
        state.isRunning = true;
        state.shouldStop = false;
    }

    private synchronized void setStopped() {
        state.isRunning = false;
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
}
