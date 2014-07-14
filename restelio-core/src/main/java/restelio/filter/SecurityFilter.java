package restelio.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.router.exception.RestException;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RequestContext;

// TODO: Implement security filter
public class SecurityFilter implements RouteFilter {

    static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void apply(RequestContext context, RouteFilterChain chain) throws RestException {

    }

}
