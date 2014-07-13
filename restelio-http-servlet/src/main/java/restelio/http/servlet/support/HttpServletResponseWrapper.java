package restelio.http.servlet.support;

import restelio.support.RestelioResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class HttpServletResponseWrapper extends RestelioResponse {

    private HttpServletResponse response;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

}
