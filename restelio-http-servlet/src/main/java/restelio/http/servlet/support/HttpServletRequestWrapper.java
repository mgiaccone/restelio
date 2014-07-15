/*
 * Copyright 2014 Matteo Giaccone and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
