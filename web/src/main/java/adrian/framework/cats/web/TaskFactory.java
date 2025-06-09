package adrian.framework.cats.web;

import java.net.Socket;

public interface TaskFactory {
    Runnable createTask(Socket socket);
}
