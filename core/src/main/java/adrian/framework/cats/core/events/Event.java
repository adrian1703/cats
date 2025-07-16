package adrian.framework.cats.core.events;

import net.jcip.annotations.Immutable;

import java.io.Serializable;

@Immutable
public abstract class Event implements Serializable {

    private final String guid = java.util.UUID.randomUUID()
                                              .toString();
    private final String description;

    protected Event(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
