package adrian.framework.cats.core;

/**
 * Events are the only boundary communication tool.
 *
 * @param <A>
 * @param <B>
 */
public abstract class Event<A extends Command, B> {
    private final A   sourceCommand;
    private final B   data;
    private final int returnCode;
    private final String description;

    protected Event(A sourceCommand, B data, String description, int returnCode) {
        this.sourceCommand = sourceCommand;
        this.data          = data;
        this.description   = description;
        this.returnCode    = returnCode;
    }

    public String getDescription() {
        return description;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public B getData() {
        return data;
    }

    public A getSourceCommand() {
        return sourceCommand;
    }
}
