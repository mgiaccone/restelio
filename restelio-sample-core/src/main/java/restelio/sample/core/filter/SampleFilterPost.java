package restelio.sample.core.filter;

import restelio.Restelio.HttpMethod;
import restelio.annotation.RestelioFilter;
import restelio.router.RouteFilter;

@RestelioFilter(value = "/", method = HttpMethod.GET)
public class SampleFilterPost implements RouteFilter {

}
