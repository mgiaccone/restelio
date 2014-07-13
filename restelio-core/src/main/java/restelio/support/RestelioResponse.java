package restelio.support;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RestelioResponse {

    public abstract OutputStream getOutputStream() throws IOException;

}
