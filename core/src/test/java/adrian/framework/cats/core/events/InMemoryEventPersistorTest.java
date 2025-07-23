package adrian.framework.cats.core.events;

class InMemoryEventPersistorTest extends EventPersistorTest {

    @Override
    protected EventPersistor createPersister() {
        return new InMemoryEventPersistor();
    }
}