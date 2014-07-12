package restelio.router;

public class RestelioRouter {

    public static final ThreadLocal<RestelioContext> context = new ThreadLocal<RestelioContext>();

    //private static List<LifecycleListener> lifecycleListeners =

    private static RestelioRouter instance;

    private RestelioRouter() {

    }

    public static RestelioRouter configure() {
        if (instance != null) {
            throw new RuntimeException("Restelio router is already configured");
        }
        instance = new RestelioRouter();
        // FIXME: inject configuration (somehow...)

        return instance;
    }

    public static RestelioRouter get() {
        if (instance == null) {
            throw new RuntimeException("Restelio router is not configured yet");
        }
        return instance;
    }

    public void route(RestelioRequest request, RestelioResponse response) {
        try {
            // Create context for the current request
            RestelioContext ctx = new RestelioContext(request, response);
            context.set(ctx);

            // Call lifecycle listeners
            // Run filters
            // Match request and instantiate the resource handler
            // Handle the request
        } finally {
            // Clear the thread local context
            context.remove();
        }
    }

    /**
     * Get the request context
     * @return the current thread local context
     */
    protected RestelioContext getContext() {
        return context.get();
    }

}
