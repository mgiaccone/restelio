package restelio.router;

import restelio.router.RouteRegistry.RouteInfo;

/**
 * Exception to report route registration issues
 * @author Matteo Giaccone
 */
public class RouteRegistrationException extends RuntimeException {

    public RouteRegistrationException(String message, RouteInfo routeInfo) {
        super(String.format("%s <%s>", message, routeInfo.toString()));
    }

}
