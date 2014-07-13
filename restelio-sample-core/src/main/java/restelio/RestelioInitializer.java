package restelio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.router.RouteRegistry.RouteCallback;

public class RestelioInitializer {

    static final Logger log = LoggerFactory.getLogger(RestelioInitializer.class);

    private Restelio restelio;

    public RestelioInitializer(Restelio restelio) {
        this.restelio = restelio;
    }

    public void initialize() {
        log.info("Restelio initializer started");

        log.info("Initializing routes...");
        initializeRouteRegistry();

        log.info("Initialization done!");
    }

    private void initializeRouteRegistry() {
        restelio.registerRoute(HttpMethod.GET, "/route1", new RouteCallback() {});
        restelio.registerRoute(HttpMethod.GET, "/route2", new RouteCallback() {});
    }

}
