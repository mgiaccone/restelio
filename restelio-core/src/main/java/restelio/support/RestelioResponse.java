package restelio.support;

import restelio.Restelio.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RestelioResponse {

    public abstract void sendError(HttpStatus status);

    public abstract OutputStream getOutputStream() throws IOException;

}
