package restelio.filter;

import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RestelioContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

// TODO: Implement response serializer filter (JSON only initially)
public class ResponseSerializerFilter implements RouteFilter {

    @Override
    public void apply(RestelioRequest request, RestelioResponse response, RestelioContext context, RouteFilterChain chain) {

    }

}
