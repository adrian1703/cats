package adrian.framework.cats.core.events;

public interface ChangelogPersister {
    void persist(ChangelogEvent event);
}
