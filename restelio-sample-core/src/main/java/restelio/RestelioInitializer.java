package restelio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.router.RouteRegistry.RouteCallback;
import restelio.filter.SecurityFilter;

public class RestelioInitializer {

    static final Logger log = LoggerFactory.getLogger(RestelioInitializer.class);

    private static final String PATTERN_MATCH_ALL = "/*";

    private Restelio restelio;

    public RestelioInitializer(Restelio restelio) {
        this.restelio = restelio;
    }

    public void initialize() {
        log.info("Initializing default components...");
        initializeDefaultComponents();

        log.info("Initializing routes...");
        initializeRouteRegistry();

    }

    private void initializeDefaultComponents() {
        restelio.registerFilter(PATTERN_MATCH_ALL, -1000, new SecurityFilter());
    }

    private void initializeRouteRegistry() {
        restelio.registerRoute(HttpMethod.GET, "/route1", new RouteCallback() {});
        restelio.registerRoute(HttpMethod.GET, "/route2", new RouteCallback() {});
    }

}
