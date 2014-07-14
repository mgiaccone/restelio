package restelio.http.servlet.support;

import com.google.common.base.Optional;
import restelio.Restelio.HttpMethod;
import restelio.support.RestelioRequest;
import restelio.support.Url;

import javax.servlet.http.HttpServletRequest;

/**
 * A wrapper for an HTTPServletRequest
 */
public class HttpServletRequestWrapper extends RestelioRequest<HttpServletRequest> {

    private HttpServletRequest request;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    protected Url convertToUrl(HttpServletRequest request) {
        return Url.builder(request.getRequestURL().toString())
                .queryString(Optional.fromNullable(request.getQueryString()))
                .build();
    }

    @Override
    protected HttpMethod extractHttpMethod() {
        String method = request.getMethod();
        if (HttpMethod.GET.name().equals(method)) {
            return HttpMethod.GET;
        } else if (HttpMethod.POST.name().equals(method)) {
            return HttpMethod.POST;
        } else if (HttpMethod.PUT.name().equals(method)) {
            return HttpMethod.PUT;
        } else if (HttpMethod.DELETE.name().equals(method)) {
            return HttpMethod.DELETE;
        }
        return null;
    }

    @Override
    public Optional<String> getHeader(String name) {
        return Optional.fromNullable(request.getHeader(name));
    }

    @Override
    public Optional<String> getQueryString() {
        return Optional.fromNullable(request.getQueryString());
    }

    @Override
    public Optional<String> getBasePath() {
        return Optional.fromNullable(request.getServletPath());
    }

    @Override
    public Optional<String> getRelativePath() {
        return Optional.fromNullable(request.getContextPath());
    }

}
