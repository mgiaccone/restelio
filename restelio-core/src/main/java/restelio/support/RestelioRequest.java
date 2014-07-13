package restelio.support;

import com.google.common.base.Optional;

public abstract class RestelioRequest {

    public abstract String getScheme();
    public abstract String getHostname();
    public abstract int getPort();

    public abstract Optional<String> getParameter(String name);
    public abstract Optional<String> getHeader(String name);

    public abstract String printRequest(boolean includeQuery);


}
