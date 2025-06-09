package adrian.framework.cats.web

import spock.lang.*
import java.net.Socket
import java.net.ServerSocket
import static org.mockito.Mockito.*

class SocketEndpunktTest extends Specification {

    def "should start in CREATED state"() {
        given:
        def factory = Mock(TaskFactory)
        def endpoint = new SocketEndpunkt(0, factory)

        expect:
        endpoint.getState() == 0 // State.CREATED
    }

    def "should accept a socket and run task"() {
        given:
        def port = 0 // use 0 to get any free port
        def runnable = Mock(Runnable)
        def factory = Mock(TaskFactory)
        def endpoint = new SocketEndpunkt(port, factory)

        when:
        endpoint.startListener()
        sleep(100) // let server start

        def client = new Socket("localhost", findActualPort(endpoint))
        1 * factory.createTask(_) >> runnable
        1 * runnable.run()

        and:
        endpoint.signalStop()

        then:
        noExceptionThrown()
        eventually {
            endpoint.getState() == 3 // State.IS_STOPPED
        }

        cleanup:
        client?.close()
    }

    def "calling signalStop before startListener completes still stops safely"() {
        given:
        def port = 0
        def factory = Mock(TaskFactory)
        def endpoint = new SocketEndpunkt(port, factory)

        when:
        endpoint.startListener()
        endpoint.signalStop()

        then:
        noExceptionThrown()
    }

    // Util: find port actually bound to the socket
    int findActualPort(SocketEndpunkt endpoint) {
        def field = SocketEndpunkt.State.getDeclaredField("serverSocket")
        field.setAccessible(true)
        def socket = field.get(endpoint.state)
        return socket?.getLocalPort()
    }

    // Utility: wait for a condition with timeout
    def eventually(Closure c, long timeoutMs = 1000, long pollMs = 50) {
        long start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                c.call()
                return true
            } catch (AssertionError e) {
                sleep(pollMs)
            }
        }
        throw new AssertionError("Condition not met in time.")
    }
    def "startListener should accept one socket and then tear down"() {
        given:
        // 1) make every `new ServerSocket(...)` return our mock
        def serverSocketMock = GroovyMock(ServerSocket, global: true)
        // 2) first accept() yields a real Socket, 2nd accept() throws to break loop
        def fakeSocket = Stub(Socket)
        serverSocketMock.accept() >>> [ fakeSocket, { throw new IOException("stop!") } ]
        serverSocketMock.close() >> { /* no-op */ }

        // 3) stub your TaskFactory so it returns a Runnable you can verify
        def fakeTask = Mock(Runnable)
        def tf = Stub(TaskFactory) {
            createTask(_ as Socket) >> fakeTask
        }

        // 4) build your endpoint with a real port (ignored, because of the mock above)
        def ep = new SocketEndpunkt(12345, tf)

        when:
        ep.startListener()
        // wait a little for the thread to run one accept() + task.run()
        Thread.sleep(200)

        then:
        1 * fakeTask.run()
    }

    def "should mock ServerSocket constructor and run task"() {
        given:
        def mockSocket = Mock(Socket)
        def task = Mock(Runnable)
        def taskFactory = Mock(TaskFactory) {
            createTask(mockSocket) >> task
        }

        when:
        def mockedConstruction = mockConstruction(ServerSocket) { mock, context ->
            mock.accept() >> mockSocket
        }

        def endpoint = new SocketEndpunkt(1234, taskFactory)
        endpoint.startListener()
        Thread.sleep(20)
        // simulate stopping the server
        endpoint.signalStop()

        then:
        noExceptionThrown()
        1 * task.run()

        cleanup:
        mockedConstruction.close()
    }
}