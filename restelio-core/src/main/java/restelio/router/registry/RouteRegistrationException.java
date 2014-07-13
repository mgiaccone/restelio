package restelio.router.registry;

import restelio.router.registry.RouteRegistry.RouteInfo;

public class RouteRegistrationException extends RuntimeException {

    public RouteRegistrationException(String message, RouteInfo routeInfo) {
        super(String.format("%s <%s>", message, routeInfo.toString()));
    }

}
