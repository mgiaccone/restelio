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

package restelio.support;

import com.google.common.base.Optional;
import restelio.Restelio.HttpMethod;

import static com.google.common.base.Preconditions.*;

/**
 * The request as seen by the framework
 */
public abstract class RestelioRequest<T> {

    private HttpMethod method;
    private Url url;

    protected RestelioRequest(T source) {
        this.url = convertToUrl(source);
    }

    /**
     *
     * @param source Source of request details
     * @return A wrappet Url
     */
    protected abstract Url convertToUrl(T source);

    protected abstract HttpMethod extractHttpMethod();

    /**
     * @param name Name of the header
     * @return An optional value for the specified header
     */
    public abstract Optional<String> getHeader(String name);

    /**
     * @return The request's query string
     */
    public abstract Optional<String> getQueryString();

    /**
     * Return the base path of the request. In a servlet container it is usually the path where the dispatcher
     * servlet is mapped to
     * @return the base path
     */
    public abstract Optional<String> getBasePath();

    /**
     * Return the base path of the request. In a servlet container, it is usually the path where the dispatcher
     * servlet is mapped to
     * @return the base path
     */
    public abstract Optional<String> getRelativePath();

    /**
     * @param name Name of the parameter
     * @return An optional value for the specified parameter
     */
    public Optional<String> getParameter(String name) {
        return url.getParameter(name);
    }

    /**
     * Get the request method
     * @return The framework compatible request method
     */
    public HttpMethod getMethod() {
        if (method == null) {
            this.method = extractHttpMethod();
            checkNotNull(this.method);
        }
        return method;
    }

    /**
     * @return The request's scheme
     */
    public String getScheme() {
        return url.getProtocol();
    }

    /**
     * @return The request's hostname
     */
    public String getHostname() {
        return url.getHost();
    }

    /**
     * @return The request's port
     */
    public int getPort() {
        return url.getPort();
    }

    /**
     * Get the Url abstraction wrapper instance
     * @return
     */
    public Url getUrl() {
        return url;
    }

    protected void setUrl(Url url) {
        this.url = url;
    }

    /**
     * Return the full path of the request built by the concatenation of the base path and the relative path
     * @return the full path
     */
    public String getPath() {
        return url.getPath();
    }

    /**
     * Convenience method to log
     * servlet is mapped to
     * @return the base path
     */
    public String printRequest() {
        // TODO: Implement debug request log
        return null;
    }

}
