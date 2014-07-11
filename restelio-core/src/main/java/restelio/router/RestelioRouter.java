package restelio.router;

import restelio.core.RestelioContext;
import restelio.core.RestelioRequest;
import restelio.core.RestelioResponse;

public class RestelioRouter {

    public static final ThreadLocal<RestelioContext> context = new ThreadLocal<RestelioContext>();

    //private static List<LifecycleListener> lifecycleListeners =

    public void route(RestelioRequest request, RestelioResponse response) {
        // Call lifecycle listeners
        // Run filters
        // Match request and instantiate the resource handler
        // Handle the request
    }

}
