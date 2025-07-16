package adrian.framework.cats.core.events;

public class SystemTimeService implements TimeService {
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
