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

/**
 * The core router, it handles filter and resources lifecycle
 */
public class RouteHandler implements SubscriberExceptionHandler {

    static final Logger log = LoggerFactory.getLogger(RouteHandler.class);

    private static final ThreadLocal<RestelioContext> context = new ThreadLocal<RestelioContext>();

    private final RouteRegistry routeRegistry;
    private final RouteFilterChain filterChain;
    private final EventBus eventBus;

    public RouteHandler() {
        this.routeRegistry = new RouteRegistry();
        this.filterChain = new RouteFilterChain();
        this.eventBus = new EventBus(this);

        // Register event subscribers
        eventBus.register(new DefaultLifecycleEventListener());
    }

    public void handle(RestelioRequest request, RestelioResponse response) {
        // Prepare the handler
        prepareHandler(request, response);

        //Url url = Url.
        postEvent(LifecycleEvent.requestStarted());
        try {
            // TODO: Run filters
            // TODO: Match request and instantiate the resource handler
            // TODO: Handle the request

        } finally {
            cleanupHandler();
            // Clear the thread local context
            context.remove();
        }
        postEvent(LifecycleEvent.requestCompleted());
    }

    /**
     * Event bus exception handler
     * @param throwable A exception caught from the subscriber
     * @param context The subscriber context
     */
    @Override
    public void handleException(Throwable throwable, SubscriberExceptionContext context) {

    }

    /**
     * Register a new route to be handled
     * @param method
     * @param path
     * @param callback
     */
    public void registerRoute(HttpMethod method, String path, RouteCallback callback) {
        routeRegistry.register(method, path, callback);
    }

    /**
     * Register a filter instance to be applied when the request match the specified pattern and any method
     * @param pattern The URL pattern to apply the filter to
     * @param instance The instance of the filter
     */
    public void registerFilter(String pattern, int order, RouteFilter instance) {
        registerFilter(null, pattern, order, instance);
    }

    /**
     * Register a filter instance to be applied when the request match the specified pattern/method pair
     * @param methods The HTTP method(s) to apply the filter to, set to null to always apply
     * @param pattern The URL pattern to apply the filter to
     * @param instance The instance of the filter
     */
    public void registerFilter(HttpMethod[] methods, String pattern, int order, RouteFilter instance) {
        // FIXME: Do registration of filter
    }

    /**
     * Post a new event to the handler's event bus
     * @param event The event
     */
    public <T extends Event> void postEvent(T event) {
        eventBus.post(event);
    }

    /**
     * Get the request context
     * @return the current thread local context
     */
    public RestelioContext getContext() {
        return context.get();
    }

    /**
     * Get the route registry instance
     * @return the route registry
     */
    public RouteRegistry getRouteRegistry() {
        return routeRegistry;
    }

    private void prepareHandler(RestelioRequest request, RestelioResponse response) {
        // Create the request context (local to the request thread)
        context.set(new RestelioContext(request, response));
        postEvent(LifecycleEvent.handlerReady());
    }

    private void cleanupHandler() {
        context.remove();
        postEvent(LifecycleEvent.handlerCleaned());
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
        public void onHandlerReady() {
            super.onHandlerReady();
            if (log.isTraceEnabled()) {
                log.trace("onHandlerReady()");
            }
        }

        @Override
        public void onHandlerCleanup() {
            super.onHandlerCleanup();
            if (log.isTraceEnabled()) {
                log.trace("onHandlerCleanup()");
            }
        }
    }

}
