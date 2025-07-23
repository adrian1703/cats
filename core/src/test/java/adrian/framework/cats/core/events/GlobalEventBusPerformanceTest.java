package adrian.framework.cats.core.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalEventBusPerformanceTest {

    private GlobalEventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new GlobalEventBus();
    }

    @Disabled
    @Test
    void testEventSubmissionPerformance() throws InterruptedException {
        int            totalEventsPerThread = 1_000_000;
        int            threadCount          = 5;
        CountDownLatch startGate            = new CountDownLatch(1);
        CountDownLatch endGate              = new CountDownLatch(threadCount);


        List<Event> events = new ArrayList<>(totalEventsPerThread);
        for (int i = 0; i < totalEventsPerThread; i++) {
            events.add(new TestChangelogEvent("Event " + i));
        }


        Runnable submitTask = () -> {
            try {
                startGate.await();
                for (Event event : events) {
                    eventBus.submit(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread()
                      .interrupt();
            } finally {
                endGate.countDown();
            }
        };


        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(submitTask);
            thread.start();
        }


        long startTime = System.nanoTime();
        eventBus.start();
        while (!eventBus.distributorIsRunning()) {
            //wait()
        }
        startGate.countDown();
        boolean finishedInTime = endGate.await(5, TimeUnit.SECONDS);
        long    endTime        = System.nanoTime();


        long elapsedTimeMilliseconds = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        long eventCount              = (long) totalEventsPerThread * threadCount;
        System.out.println("Total events submitted: " + eventCount);
        System.out.println("Elapsed time (ms): " + elapsedTimeMilliseconds);
        System.out.println("Time per e submitted (ms): " + elapsedTimeMilliseconds / eventCount);


        assertTrue(finishedInTime, "All threads should finish within 5 seconds");
    }
}