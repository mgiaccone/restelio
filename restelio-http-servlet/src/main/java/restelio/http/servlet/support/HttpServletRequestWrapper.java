package restelio.http.servlet.support;

import com.google.common.base.Optional;
import restelio.support.RestelioRequest;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper extends RestelioRequest {

    private HttpServletRequest request;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getHostname() {
        // FIXME: Handle X-Forwarded-For header
        return request.getServerName();
    }

    @Override
    public int getPort() {
        return request.getServerPort();
    }

    @Override
    public Optional<String> getParameter(String name) {
        // FIXME: Process body params as well if the request is x-www-formencoded
        return Optional.fromNullable(request.getParameter(name));
    }

    @Override
    public Optional<String> getHeader(String name) {
        return Optional.fromNullable(request.getHeader(name));
    }

    @Override
    public Optional<String> getQueryString() {
        return null;
    }

    @Override
    public Optional<String> getBasePath() {
        return null;
    }

    @Override
    public Optional<String> getRelativePath() {
        return null;
    }

    @Override
    public Optional<String> getPath() {
        return null;
    }
}
