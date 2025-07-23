package adrian.framework.cats.core.time;

public class SystemTimeService implements TimeService {
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
