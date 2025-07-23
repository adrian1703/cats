package adrian.framework.cats.core.events;

import adrian.framework.cats.core.time.TimeService;

public abstract class ChangelogEvent extends Event implements Runnable {

    private final long     changelogId;
    private final Object[] locks;
    private       boolean  hasBeenApplied = false;

    protected ChangelogEvent(ChangelogIdGenerator idGenerator, String description, Object... locks) {
        super(description);
        this.changelogId = idGenerator.nextId();
        this.locks       = locks;
    }

    protected ChangelogEvent(ChangelogIdGenerator idGenerator, TimeService timeService, String description, Object... locks) {
        super(timeService, description);
        this.changelogId = idGenerator.nextId();
        this.locks       = locks;
    }
}
