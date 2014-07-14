package restelio.sample.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.annotation.RestelioFilter;
import restelio.router.exception.RestException;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RequestContext;

@RestelioFilter(value = "/*")
public class SampleFilterAll implements RouteFilter {

    static final Logger log = LoggerFactory.getLogger(SampleFilterAll.class);

    @Override
    public void apply(RequestContext context, RouteFilterChain chain) throws RestException {

    }

}
