package restelio.support;

import com.google.common.base.Optional;

/**
 * The request as seen by the framework
 */
public abstract class RestelioRequest {

    /**
     * @return The request's scheme
     */
    public abstract String getScheme();

    /**
     * @return The request's hostname
     */
    public abstract String getHostname();

    /**
     * @return The request's port
     */
    public abstract int getPort();

    /**
     * @param name Name of the parameter
     * @return An optional value for the specified parameter
     */
    public abstract Optional<String> getParameter(String name);

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
     * Return the full path of the request built by the concatenation of the base path and the relative path
     * @return the full path
     */
    public abstract Optional<String> getPath();

    /**
     * Convenience method to log
     * servlet is mapped to
     * @return the base path
     */
    public String printRequest() {
        return null;
    }


}
