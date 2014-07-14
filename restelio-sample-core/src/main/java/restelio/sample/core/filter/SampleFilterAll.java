package restelio.sample.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.annotation.RestelioFilter;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RestelioContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

@RestelioFilter(value = "/*")
public class SampleFilterAll implements RouteFilter {

    static final Logger log = LoggerFactory.getLogger(SampleFilterAll.class);

    @Override
    public void apply(RestelioRequest request, RestelioResponse response, RestelioContext context, RouteFilterChain chain) {

    }

}
