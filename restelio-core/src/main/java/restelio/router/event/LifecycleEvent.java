package restelio.router.event;

public class LifecycleEvent implements Event {

    public static final int REQUEST_STARTED     = 0;
    public static final int REQUEST_COMPLETED   = 1;
    public static final int HANDLER_READY       = 2;
    public static final int HANDLER_CLEANUP     = 3;

    public static LifecycleEvent requestStarted(final String path) {
        return new LifecycleEvent(REQUEST_STARTED, path);
    }

    public static LifecycleEvent requestCompleted(final String path) {
        return new LifecycleEvent(REQUEST_COMPLETED, path);
    }

    public static LifecycleEvent handlerReady(final String path) {
        return new LifecycleEvent(HANDLER_READY, path);
    }

    public static LifecycleEvent handlerCleaned(final String path) {
        return new LifecycleEvent(HANDLER_CLEANUP, path);
    }

    private int eventId;
    private String path;

    public LifecycleEvent(int eventId, String path) {
        this.eventId = eventId;
        this.path = path;
    }

    public int getEventId() {
        return eventId;
    }

    public String getPath() {
        return path;
    }
}
