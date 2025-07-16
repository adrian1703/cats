package adrian.framework.cats.core.events;

import java.io.Serializable;

public abstract class ChangelogEvent extends Event implements Runnable, Serializable {

    private final long     changelogId;
    private final Object[] locks;
    private       boolean  hasBeenApplied = false;

    protected ChangelogEvent(ChangelogIdGenerator idGenerator, String description, Object... locks) {
        super(description);
        this.changelogId = idGenerator.nextId();
        this.locks       = locks;
    }
}
