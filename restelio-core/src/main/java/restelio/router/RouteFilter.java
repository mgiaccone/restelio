package restelio.router;

import restelio.support.RestelioContext;
import restelio.support.RestelioRequest;
import restelio.support.RestelioResponse;

public interface RouteFilter {

    void apply(RestelioRequest request, RestelioResponse response, RestelioContext context, RouteFilterChain chain);

}
