package restelio.router;

/**
 * Exception to be used when a route is not found in the registry
 * @author Matteo Giaccone
 */
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String path) {
        super(String.format("Path not found: %s", path));
    }

}
