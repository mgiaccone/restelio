package restelio.filter;

import restelio.router.exception.RestException;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RequestContext;

// TODO: Implement GZip compression filter
public class GZipResponseFilter implements RouteFilter {

    @Override
    public void apply(RequestContext context, RouteFilterChain chain) throws RestException {

    }

}
