package restelio.router;

import com.google.common.base.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.router.RouteRegistry.RouteCallback;
import restelio.router.RouteRegistry.RouteMatch;
import restelio.router.exception.RestException;
import restelio.router.exception.RouteRegistrationException;
import restelio.support.RequestContext;

import static org.junit.Assert.*;

public class RouteRegistryTest {

    static final Logger log = LoggerFactory.getLogger(RouteRegistryTest.class);

    private static RouteRegistry registry;

    private static RouteCallback mockRouteCallback() {
        return new RouteCallback() {
            @Override
            public void execute(Object instance, RequestContext context) throws RestException {

            }
        };
    }

    private static Object mockResourceInstance() {
        return new Object();
    }

    @BeforeClass
    public static void setup() {
        registry = new RouteRegistry();

        // Initialize test routes
        registry.register(HttpMethod.GET, "/", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.POST, "/", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.GET, "/users", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.POST, "/users/{id}", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.PUT, "/users/{id}", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockResourceInstance(), mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}/comments/12345/replies/560697/dates", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnRootNode() {
        registry.register(HttpMethod.GET, "/", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnSegmentNode() {
        registry.register(HttpMethod.GET, "/users", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnParameterNode() {
        registry.register(HttpMethod.GET, "/users/{id}", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnInnerSegmentNode() {
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnInnerParameterNode() {
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockResourceInstance(), mockRouteCallback());
    }

    @Test(expected = RouteRegistrationException.class)
    public void testRouteRegistrationExceptionOnDuplicateParameter() {
        registry.register(HttpMethod.GET, "/users/{id}/friends/{id}", mockResourceInstance(), mockRouteCallback());
    }

    @Test
    public void testRouteNotFoundExceptionOnRootNode() {
        Optional<RouteMatch> match = registry.find(HttpMethod.PUT, "/");
        assertFalse(match.isPresent());
    }

    @Test
    public void testRouteNotFoundExceptionOnSegmentNode() {
        Optional<RouteMatch> match = registry.find(HttpMethod.PUT, "/users");
        assertFalse(match.isPresent());
    }

    @Test
    public void testRouteNotFoundExceptionOnParameterNode() {
        Optional<RouteMatch> match = registry.find(HttpMethod.DELETE, "/users/12345");
        assertFalse(match.isPresent());
    }

    @Test
    public void testRouteNotFoundExceptionOnInnerSegmentNode() {
        Optional<RouteMatch> match = registry.find(HttpMethod.PUT, "/users/01234/orders");
        assertFalse(match.isPresent());
    }

    @Test
    public void testRouteNotFoundExceptionOnInnerParameterNode() {
        Optional<RouteMatch> match = registry.find(HttpMethod.PUT, "/users/01234/orders/56789");
        assertFalse(match.isPresent());
    }

    @Test
    public void testRouteNotFoundExceptionOnInnerExtraSegment() {
        Optional<RouteMatch> match = registry.find(HttpMethod.GET, "/users/01234/orders/56789/comments");
        assertFalse(match.isPresent());
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
