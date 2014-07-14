package restelio.support;

import restelio.Restelio.HttpStatus;

public class RequestContext {

    private final RestelioRequest request;
    private final RestelioResponse response;

    public RequestContext(RestelioRequest request, RestelioResponse response) {
        this.request = request;
        this.response = response;
    }

    public RestelioRequest getRequest() {
        return request;
    }

    public RestelioResponse getResponse() {
        return response;
    }

    public void sendError(HttpStatus status) {
        response.sendError(status);
    }

    public String getPath() {
        return request.getPath();
    }

}
