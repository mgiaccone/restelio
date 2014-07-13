package restelio.sample.core;

import restelio.annotation.GET;
import restelio.annotation.RestelioResource;

@RestelioResource
public class SampleResource {

    @GET
    public SampleResponse getTestNoParameter() {
        return new SampleResponse();
    }
}
