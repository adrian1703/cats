package adrian.framework.cats.core;

public interface ChangelogPersister {
    void persist(ChangeEvent event);
}
