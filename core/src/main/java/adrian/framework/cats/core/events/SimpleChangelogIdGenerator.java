package adrian.framework.cats.core.events;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicLong;

@ThreadSafe
public class SimpleChangelogIdGenerator implements ChangelogIdGenerator {

    private final AtomicLong currenId = new AtomicLong(0);

    SimpleChangelogIdGenerator() {
    }

    SimpleChangelogIdGenerator(Long currenId) {
        this.currenId.set(currenId);
    }

    @Override
    public long nextId() {
        return currenId.incrementAndGet();
    }
}
