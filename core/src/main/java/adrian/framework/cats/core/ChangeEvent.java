package adrian.framework.cats.core;

import java.io.Serializable;

public abstract class ChangeEvent extends Event implements Runnable, Serializable {

    private final long     changelogId;
    private final Object[] locks;
    private       boolean  hasBeenApplied = false;

    protected ChangeEvent(ChangelogIdGenerator idGenerator, String description, Object... locks) {
        super(description);
        this.changelogId = idGenerator.nextId();
        this.locks       = locks;
    }
}
