package adrian.framework.cats.core.events;

import java.util.concurrent.ExecutorService;

public class ChangelogApplicator implements EventListener {

    private final Helper helper;

    private record Helper(ExecutorService executorService,
                          EventPersistor<ChangelogEvent> eventpersistor) {}

    public ChangelogApplicator(ExecutorService executorService,
                               EventPersistor<ChangelogEvent> eventPersistor) {
        this.helper = new Helper(executorService, eventPersistor);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof ChangelogEvent changeEvent)) {
            return;
        }
        helper.eventpersistor.persist(changeEvent);
        helper.executorService.submit((changeEvent));
    }
}
