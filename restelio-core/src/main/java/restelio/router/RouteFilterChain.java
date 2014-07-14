package restelio.router;

import restelio.Restelio.HttpMethod;

public class RouteFilterChain {

    //private List<FilterInfo>

    /**
     * Filter metadata
     */
    public static class FilterInfo {

        private String matchPattern;
        private HttpMethod method;


    }
}
