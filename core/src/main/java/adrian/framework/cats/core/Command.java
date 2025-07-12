package adrian.framework.cats.core;

import java.io.Serializable;

public abstract class Command implements Serializable {
    final long id;
    final boolean isTransient;

    protected Command(long id, boolean isTransient) {
        this.id          = id;
        this.isTransient = isTransient;
    }
}
