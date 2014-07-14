package restelio.router;

import restelio.router.exception.RestException;
import restelio.support.RequestContext;

public interface RouteFilter {

    void apply(RequestContext context, RouteFilterChain chain) throws RestException;

}
