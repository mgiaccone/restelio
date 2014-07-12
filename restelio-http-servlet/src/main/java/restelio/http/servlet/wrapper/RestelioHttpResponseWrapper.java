package restelio.http.servlet.wrapper;

import restelio.router.RestelioRequest;

import javax.servlet.http.HttpServletRequest;

public class RestelioHttpResponseWrapper extends RestelioRequest {

    private HttpServletRequest request;

    public RestelioHttpResponseWrapper(HttpServletRequest request) {
        this.request = request;
    }

}
