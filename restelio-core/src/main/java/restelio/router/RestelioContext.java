package restelio.router;

public class RestelioContext {

    private final RestelioRequest request;
    private final RestelioResponse response;

    public RestelioContext(RestelioRequest request, RestelioResponse response) {
        this.request = request;
        this.response = response;
    }

    public RestelioRequest getRequest() {
        return request;
    }

    public RestelioResponse getResponse() {
        return response;
    }

}
