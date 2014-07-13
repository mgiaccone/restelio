package restelio.listener;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.router.event.LifecycleEvent;

/**
 * Request lifecycle event listener
 * @author Matteo Giaccone
 */
public abstract class LifecycleEventListener {

    static final Logger log = LoggerFactory.getLogger(LifecycleEventListener.class);

    static final ThreadLocal<Stopwatch> stopwatch = new ThreadLocal<Stopwatch>();

    public void onRequestStarted() {
        // Default empty implementation
    }

    public void onRequestCompleted() {
        // Default empty implementation
    }

    public void onContextCreated() {
        // Default empty implementation
    }

    @Subscribe
    public final void handle(LifecycleEvent event) {
        switch (event.getEventId()) {
            case LifecycleEvent.REQUEST_STARTED:
                stopwatch.set(Stopwatch.createStarted());
                onRequestStarted();
                break;

            case LifecycleEvent.REQUEST_COMPLETED:
                onRequestCompleted();
                stopwatch.get().stop();
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Request completed in %s", stopwatch.get().toString()));
                }
                stopwatch.remove();
                break;

            case LifecycleEvent.CONTEXT_CREATED:
                onContextCreated();
                break;

            default:
                log.info("Unknown lifecycle event: " + event.getEventId());
                break;
        }
    }

}
