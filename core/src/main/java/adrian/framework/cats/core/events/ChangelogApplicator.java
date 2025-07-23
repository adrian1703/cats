package adrian.framework.cats.core.events;

import java.util.concurrent.ExecutorService;

public class ChangelogApplicator implements EventListener {

    private final ExecutorService executorService;

    public ChangelogApplicator(ExecutorService executorService) {this.executorService = executorService;}

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof ChangelogEvent)) {
            return;
        }
        executorService.submit(((ChangelogEvent) event));
    }
}
