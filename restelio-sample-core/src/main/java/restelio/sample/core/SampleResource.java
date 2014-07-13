package restelio.sample.core;

import restelio.annotation.*;

@RestelioResource
public class SampleResource {

    @GET("/get")
    public SampleResponse getTestNoParameter() {
        return new SampleResponse();
    }

    @POST("/post")
    public SampleResponse postTestNoParameter() {
        return new SampleResponse();
    }

    @PUT("/put")
    public SampleResponse putTestNoParameter() {
        return new SampleResponse();
    }

    @DELETE("/delete")
    public SampleResponse deleteTestNoParameter() {
        return new SampleResponse();
    }

}
