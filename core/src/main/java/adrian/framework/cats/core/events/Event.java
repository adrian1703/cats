package adrian.framework.cats.core.events;

import net.jcip.annotations.Immutable;

import java.io.Serializable;

@Immutable
public abstract class Event implements Serializable {

    private final String guid = java.util.UUID.randomUUID()
                                              .toString();
    private final String description;
    private final long   timestamp;

    protected Event(String description) {
        this.description = description;
        this.timestamp   = new SystemTimeService().now();
    }

    protected Event(TimeService timeService, String description) {
        this.description = description;
        this.timestamp   = timeService.now();
    }

    public String getDescription() {
        return description;
    }
}
