package restelio.router;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.router.RouteFilterChain.HandlerExecutionCallback;
import restelio.router.RouteRegistry.RouteCallback;
import restelio.router.RouteRegistry.RouteInfo;
import restelio.router.RouteRegistry.RouteMatch;
import restelio.router.event.Event;
import restelio.router.event.LifecycleEvent;
import restelio.router.exception.RestException;
import restelio.router.listener.LifecycleEventListener;
import restelio.support.RequestContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

/**
 * The core router, here's where the magic happens.
 * In real terms, it handles filter and resource lifecycle
 */
public class RouteHandler implements SubscriberExceptionHandler {

    static final Logger log = LoggerFactory.getLogger(RouteHandler.class);

    private static final ThreadLocal<RequestContext> context = new ThreadLocal<RequestContext>();

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

    public void handle(final RestelioRequest request, final RestelioResponse response) throws RestException {
        // Prepare the handler
        prepareHandler(request, response);

        // Start handling the request
        final String path = request.getPath();
        postEvent(LifecycleEvent.requestStarted(path));
        try {
            // Look for an existing route. If none, don't even bother to apply filters
            final Optional<RouteMatch> match = routeRegistry.find(request.getMethod(), path);
            filterChain.handle(match, getContext(), new HandlerExecutionCallback() {
                @Override
                public void executeHandler() throws RestException {
                    Optional<RouteInfo> route = match.get().getRouteInfo();
                    if (route.isPresent()) {
                        RouteInfo routeInfo = route.get();
                        Optional<RouteCallback> routeCallback = routeInfo.getCallback();
                        if (routeCallback.isPresent()) {
                            routeCallback.get().execute(routeInfo.getInstance(), getContext());
                            return;
                        }
                    }
                    // This should never happen, but just in case...
                    log.error(String.format("Weird! No route callback available for path [%s %s]",
                            request.getMethod(), request.getPath()));
                    RestException.throwNotFound();
                }
            });
        } finally {
            // Cleanup all thread local stuff after handling
            cleanupHandler(request);
        }

        // Request handling done
        postEvent(LifecycleEvent.requestCompleted(path));
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
    public void registerRoute(HttpMethod method, String path, Object instance, RouteCallback callback) {
        routeRegistry.register(method, path, instance, callback);
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
    public RequestContext getContext() {
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
        RequestContext ctx = new RequestContext(request, response);
        context.set(ctx);

        // Notify event listeners
        postEvent(LifecycleEvent.handlerReady(request.getPath()));
    }

    private void cleanupHandler(RestelioRequest request) {
        context.remove();
        postEvent(LifecycleEvent.handlerCleaned(request.getPath()));
    }


    /**
     * Lifecycle event subscriber
     */
    static class DefaultLifecycleEventListener extends LifecycleEventListener {

        static final Logger log = LoggerFactory.getLogger(DefaultLifecycleEventListener.class);

        @Override
        public void onRequestStarted(final String path) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("onRequestStarted(%s)", path));
            }
        }

        @Override
        public void onRequestCompleted(final String path) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("onRequestCompleted(%s)", path));
            }
        }

        @Override
        public void onHandlerReady(final String path) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("onHandlerReady(%s)", path));
            }
        }

        @Override
        public void onHandlerCleanup(final String path) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("onHandlerCleanup(%s)", path));
            }
        }
    }

}
