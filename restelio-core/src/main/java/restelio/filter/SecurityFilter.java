package restelio.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RestelioContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

// TODO: Implement security filter
public class SecurityFilter implements RouteFilter {

    static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void apply(RestelioRequest request, RestelioResponse response, RestelioContext context, RouteFilterChain chain) {

    }

}
