package restelio.router;

import org.junit.BeforeClass;
import org.junit.Test;
import restelio.http.HttpMethod;
import restelio.router.registry.RouteNotFoundException;
import restelio.router.registry.RouteRegistry;
import restelio.router.registry.RouteRegistry.RouteCallback;
import restelio.router.registry.DuplicateRouteException;

public class RouteRegistryTest {

    private static RouteRegistry registry;

    @BeforeClass
    public static void setup() {
        registry = RouteRegistry.get();

        // Initialize test routes
        registry.register(HttpMethod.GET, "/", mockRouteCallback());
        registry.register(HttpMethod.POST, "/", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.POST, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.PUT, "/users/{id}", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockRouteCallback());
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockRouteCallback());
    }

    private static RouteCallback mockRouteCallback() {
        return new RouteCallback() {};
    }

    @Test(expected = DuplicateRouteException.class)
    public void testDuplicateRouteExceptionOnRootNode() {
        registry.register(HttpMethod.GET, "/", mockRouteCallback());
    }

    @Test(expected = DuplicateRouteException.class)
    public void testDuplicateRouteExceptionOnSegmentNode() {
        registry.register(HttpMethod.GET, "/users", mockRouteCallback());
    }

    @Test(expected = DuplicateRouteException.class)
    public void testDuplicateRouteExceptionOnParameterNode() {
        registry.register(HttpMethod.GET, "/users/{id}", mockRouteCallback());
    }

    @Test(expected = DuplicateRouteException.class)
    public void testDuplicateRouteExceptionOnInnerSegmentNode() {
        registry.register(HttpMethod.GET, "/users/{id}/orders", mockRouteCallback());
    }

    @Test(expected = DuplicateRouteException.class)
    public void testDuplicateRouteExceptionOnInnerParameterNode() {
        registry.register(HttpMethod.GET, "/users/{userId}/orders/{orderId}", mockRouteCallback());
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
        registry.find(HttpMethod.PUT, "/users/12345");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnInnerSegmentNode() {
        registry.find(HttpMethod.PUT, "/users/01234/orders");
    }

    @Test(expected = RouteNotFoundException.class)
    public void testRouteNotFoundExceptionOnInnerParameterNode() {
        registry.find(HttpMethod.PUT, "/users/01234/orders/56789");
    }

//        @Test
//    public void multiplicationOfZeroIntegersShouldReturnZero() {
//
////        // MyClass is tested
////        MyClass tester = new MyClass();
////
////        // Tests
////        assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));
////        assertEquals("0 x 10 must be 0", 0, tester.multiply(0, 10));
////        assertEquals("0 x 0 must be 0", 0, tester.multiply(0, 0));
//    }

}
