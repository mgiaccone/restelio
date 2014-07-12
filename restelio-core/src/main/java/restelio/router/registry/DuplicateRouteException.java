package restelio.router.registry;

import restelio.router.registry.RouteRegistry.RouteInfo;

public class DuplicateRouteException extends RuntimeException {

    public DuplicateRouteException(RouteInfo routeInfo) {
        super(routeInfo.toString());
    }

}
