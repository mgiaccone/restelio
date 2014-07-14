package restelio.router.event;

public class LifecycleEvent implements Event {

    public static final int REQUEST_STARTED     = 0;
    public static final int REQUEST_COMPLETED   = 1;
    public static final int HANDLER_READY       = 2;
    public static final int HANDLER_CLEANUP = 3;

    public static LifecycleEvent requestStarted() {
        return new LifecycleEvent(REQUEST_STARTED);
    }

    public static LifecycleEvent requestCompleted() {
        return new LifecycleEvent(REQUEST_COMPLETED);
    }

    public static LifecycleEvent handlerReady() {
        return new LifecycleEvent(HANDLER_READY);
    }

    public static LifecycleEvent handlerCleaned() {
        return new LifecycleEvent(HANDLER_CLEANUP);
    }

    private int eventId;

    public LifecycleEvent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

}
