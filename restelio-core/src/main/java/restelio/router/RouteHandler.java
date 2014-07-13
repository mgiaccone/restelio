package restelio.router;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.listener.LifecycleEventListener;
import restelio.router.RouteRegistry.RouteCallback;
import restelio.router.event.Event;
import restelio.router.event.LifecycleEvent;
import restelio.support.RestelioContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

public class RouteHandler implements SubscriberExceptionHandler {

    static final Logger log = LoggerFactory.getLogger(RouteHandler.class);

    private static final ThreadLocal<RestelioContext> context = new ThreadLocal<RestelioContext>();

    private final RouteRegistry routeRegistry;
    private final EventBus eventBus;

    public RouteHandler() {
        this.routeRegistry = new RouteRegistry();
        this.eventBus = new EventBus(this);

        // Initialize event bus subscribers
        eventBus.register(new DefaultLifecycleEventListener());
    }

    public void handle(RestelioRequest request, RestelioResponse response) {
        postEvent(LifecycleEvent.requestStarted());
        try {
            // Create the request context (local to the request thread)
            context.set(createContext(request, response));
            postEvent(LifecycleEvent.contextCreated());

            // Call lifecycle listeners
            // Run filters
            // Match request and instantiate the resource handler
            // Handle the request

        } finally {
            // Clear the thread local context
            context.remove();
        }
        postEvent(LifecycleEvent.requestCompleted());
    }

    public void registerRoute(HttpMethod method, String path, RouteCallback callback) {
        routeRegistry.register(method, path, callback);
    }

    /**
     * Create the context for the current request
     *
     * @param request Restelio request
     * @param response Restelio response
     * @return A new configured context
     */
    private RestelioContext createContext(RestelioRequest request, RestelioResponse response) {
        RestelioContext context = new RestelioContext(request, response);
        return context;
    }

    @Override
    public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {

    }

    private <T extends Event> void postEvent(T event) {
        eventBus.post(event);
    }

    /**
     * Get the request context
     * @return the current thread local context
     */
    protected RestelioContext getContext() {
        return context.get();
    }

    public RouteRegistry getRouteRegistry() {
        return routeRegistry;
    }

    /**
     * Lifecycle event subscriber
     */
    static class DefaultLifecycleEventListener extends LifecycleEventListener {

        static final Logger log = LoggerFactory.getLogger(DefaultLifecycleEventListener.class);

        @Override
        public void onRequestStarted() {
            super.onRequestStarted();
            if (log.isTraceEnabled()) {
                log.trace("onRequestStarted()");
            }
        }

        @Override
        public void onRequestCompleted() {
            super.onRequestCompleted();
            if (log.isTraceEnabled()) {
                log.trace("onRequestCompleted()");
            }
        }

        @Override
        public void onContextCreated() {
            super.onContextCreated();
            if (log.isTraceEnabled()) {
                log.trace("onContextCreated()");
            }
        }
    }

}
