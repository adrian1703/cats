package adrian.framework.cats.core.events;

class InMemoryChangelogPersisterTest extends ChangelogPersisterTest {

    @Override
    protected ChangelogPersister createPersister() {
        return new InMemoryChangelogPersister();
    }
}