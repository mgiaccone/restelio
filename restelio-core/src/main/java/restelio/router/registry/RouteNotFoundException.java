package restelio.router.registry;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String path) {
        super(String.format("Path not found: %s", path));
    }

}
