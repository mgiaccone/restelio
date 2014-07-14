package restelio.filter;

import restelio.router.exception.RestException;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RequestContext;

// TODO: Implement response serializer filter (JSON only initially)
public class ResponseSerializerFilter implements RouteFilter {

    @Override
    public void apply(RequestContext context, RouteFilterChain chain) throws RestException {

    }

}
