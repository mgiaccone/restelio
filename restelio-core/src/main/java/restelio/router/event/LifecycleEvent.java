package restelio.router.event;

public class LifecycleEvent implements Event {

    public static final int REQUEST_STARTED     = 0;
    public static final int REQUEST_COMPLETED   = 1;
    public static final int CONTEXT_CREATED     = 2;

    public static LifecycleEvent requestStarted() {
        return new LifecycleEvent(REQUEST_STARTED);
    }

    public static LifecycleEvent requestCompleted() {
        return new LifecycleEvent(REQUEST_COMPLETED);
    }

    public static LifecycleEvent contextCreated() {
        return new LifecycleEvent(CONTEXT_CREATED);
    }

    private int eventId;

    public LifecycleEvent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

}
