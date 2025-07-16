package adrian.framework.cats.core.events;

public class TestChangelogEvent extends ChangelogEvent {
    static ChangelogIdGenerator idGenerator = new SimpleChangelogIdGenerator();

    public TestChangelogEvent(String description) {
        super(idGenerator, description);
    }

    @Override
    public void run() {
    }
}
