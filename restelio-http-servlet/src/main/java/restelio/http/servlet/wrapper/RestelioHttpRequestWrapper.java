package restelio.http.servlet.wrapper;

import restelio.router.RestelioRequest;

import javax.servlet.http.HttpServletRequest;

public class RestelioHttpRequestWrapper extends RestelioRequest {

    private HttpServletRequest request;

    public RestelioHttpRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

}
