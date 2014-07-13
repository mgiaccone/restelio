package restelio.router;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.router.RouteRegistry.RouteCallback;

public class RouteRegistryTest {

    static final Logger log = LoggerFactory.getLogger(RouteRegistryTest.class);

    private static RouteRegistry registry;

    private static RouteCallback mockRouteCallback() {
        return new RouteCallback() {};
    }

    @BeforeClass
    public static void setup() {
        registry = new RouteRegistry();

        // Initialize test routes
        registry.register(HttpMethod.GET, "/", mockRouteCallback());
        registry.register(HttpMethod.POST, "/", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.POST, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.PUT, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}/comments/12345/replies/560697/dates", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnRootNode() {
        registry.register(HttpMethod.GET, "/", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnSegmentNode() {
        registry.register(HttpMethod.GET, "/users", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnParameterNode() {
        registry.register(HttpMethod.GET, "/users/{id}", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnInnerSegmentNode() {
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnInnerParameterNode() {
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnDuplicateParameter() {
        registry.register(HttpMethod.GET, "/users/{id}/friends/{id}", mockRouteCallback());
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnRootNode() {
        registry.find(HttpMethod.PUT, "/");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnSegmentNode() {
        registry.find(HttpMethod.PUT, "/users");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnParameterNode() {
        registry.find(HttpMethod.DELETE, "/users/12345");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnInnerSegmentNode() {
        registry.find(HttpMethod.PUT, "/users/01234/orders");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnInnerParameterNode() {
        registry.find(HttpMethod.PUT, "/users/01234/orders/56789");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnInnerExtraSegment() {
        registry.find(HttpMethod.GET, "/users/01234/orders/56789/comments");
    }

    @Test
    public void testFindRouteOnRootNode() {
        registry.find(HttpMethod.GET, "/");
    }

    @Test
    public void testFindRouteOnSegmentNode() {
        registry.find(HttpMethod.GET, "/users");
    }

    @Test
    public void testFindRouteOnParameterNode() {
        registry.find(HttpMethod.GET, "/users/12345");
    }

    @Test
    public void testFindRouteOnInnerSegmentNode() {
        registry.find(HttpMethod.GET, "/users/12345/orders");
    }

    @Test
    public void testFindRouteOnInnerParameterNode() {
        registry.find(HttpMethod.GET, "/users/12345/orders/67890");
    }

    @Test
    public void testFindLongPathMatchRepeatedTiming() {
        long average = 0;
        for (int i = 0; i < 100; i++) {
            long start = System.nanoTime();
            registry.find(HttpMethod.GET, "/users/{userId}/orders/{orderId}/comments/12345/replies/560697/dates");
            long time = (System.nanoTime() - start) / 1000;

            if (i == 0) {
                average = time;
            } else {
                average = (average + time) / 2;
            }
        }
        log.debug(String.format("Average route match time on 1000 repetition: %dµs", average));
    }
}
