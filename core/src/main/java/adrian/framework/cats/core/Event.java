package adrian.framework.cats.core;

import net.jcip.annotations.Immutable;

@Immutable
public abstract class Event {

    private final String guid = java.util.UUID.randomUUID().toString();
    private final String description;

    protected Event(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
