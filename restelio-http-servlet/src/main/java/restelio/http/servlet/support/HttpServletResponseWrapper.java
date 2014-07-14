package restelio.http.servlet.support;

import restelio.Restelio.HttpStatus;
import restelio.support.RestelioResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A wrapper for an HTTPServletResponse
 */
public class HttpServletResponseWrapper extends RestelioResponse {

    private HttpServletResponse response;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void sendError(HttpStatus status) {
        try {
            response.sendError(status.getCode());
        } catch (IOException e) {
            // Something bad happened, we can't do much about it at this point
            e.printStackTrace();
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

}
