package restelio.router.registry;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.http.HttpMethod;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton graph based route registry
 */
public class RouteRegistry {

    static final Logger log = LoggerFactory.getLogger(RouteRegistry.class);

    private static final Pattern REGEX_PARAM = Pattern.compile("(?:\\{(\\w+)\\})");
    private static final String FORWARD_SLASH = "/";

    private static RouteRegistry instance;

    private RouteNode rootNode;

    private RouteRegistry() {
        rootNode = RouteNode.root();
    }

    /**
     * Get the static instance of the RouteRegistry
     *
     * @return A singleton instance of the RouteRegistry
     */
    public static RouteRegistry get() {
        if (instance == null) {
            instance = new RouteRegistry();
        }
        return instance;
    }

    /**
     * Register a new route to a callback
     *
     * @param method the route HTTP Method
     * @param path the route path
     * @param callback the route callback
     * @return A route information object
     */
    public RouteInfo register(HttpMethod method, String path, RouteCallback callback) {
        return register(method, Optional.<String>absent(), path, callback);
    }

    /**
     * Register a new route to a callback
     *
     * @param method the route HTTP Method
     * @param basePath the base path (usually the path where the dispatcher servlet is mapped to)
     * @param path the route path
     * @param callback the route callback
     * @return A route information object
     */
    public RouteInfo register(HttpMethod method, Optional<String> basePath, String path, RouteCallback callback) {
        // FIXME: handle duplicate parameter ids when creating routes

        RouteInfo routeInfo = registerInternal(rootNode, splitPath(basePath, path), new RouteInfo(method, path, callback));
        if (log.isDebugEnabled()) {
            log.debug(String.format("Route %s", routeInfo.toString()));
        }
        return routeInfo;
    }

    public RouteMatchResult find(HttpMethod method, String path) {
        return find(method, Optional.<String>absent(), path);
    }

    public RouteMatchResult find(HttpMethod method, Optional<String> basePath, String path) {
        RouteMatchResult result = findInternal(rootNode, method, splitPath(basePath, path), new RouteMatchResult());
        if (log.isDebugEnabled()) {
            log.debug(String.format("Result %s", result.toString()));
        }
        return result;
    }

    public RouteMatchResult findInternal(RouteNode parentNode, HttpMethod method, LinkedList<String> path, RouteMatchResult result) {
        if (path.size() > 0) {
            String segment = path.pop();
            Optional<RouteNode> child = parentNode.findNode(segment);

            return findInternal(child.get(), method, path, result);
        } else {

        }
        return result;
    }

    private RouteInfo registerInternal(RouteNode parentNode, LinkedList<String> path, RouteInfo routeInfo) {
        if (path.size() > 0) {
            String segment = path.pop();
            RouteNode child = parentNode.findOrCreateNode(segment, isParameter(segment));
            return registerInternal(child, path, routeInfo);
        } else {
            // Root or leaf node
            parentNode.setRouteInfo(routeInfo);
        }
        return routeInfo;
    }

    private LinkedList<String> splitPath(Optional<String> basePath, String path) {
        // FIXME: Handle basePath
        LinkedList<String> splitPath = Lists.newLinkedList(
                Splitter.on(FORWARD_SLASH)
                        .omitEmptyStrings()
                        .trimResults()
                        .split(path)
        );
        return splitPath;
    }

    private boolean isParameter(String segment) {
        Matcher m = REGEX_PARAM.matcher(segment);
        return (segment != null && m.matches());
    }

    /**
     * Route callback interface
     */
    public static interface RouteCallback {

    }

    /**
     * Route callback interface
     */
    public static class RouteMatchResult {

        private RouteInfo routeInfo;
        private Map<String, String> pathParameters;

        private RouteMatchResult() {
            pathParameters = Maps.newHashMap();
        }

        public RouteInfo getRouteInfo() {
            return routeInfo;
        }

        private void setRouteInfo(RouteInfo routeInfo) {
            this.routeInfo = routeInfo;
        }

        public Optional<String> getParameter(String name) {
            return Optional.fromNullable(pathParameters.get(name));
        }

        private void putParameter(String name, String value) {
            pathParameters.put(name, value);
        }

    }

    /**
     * Route information
     */
    public static class RouteInfo {

        private HttpMethod method;
        private String path;
        private RouteCallback callback;

        public RouteInfo(HttpMethod method, String path, RouteCallback callback) {
            this.method = method;
            this.path = path;
            this.callback = callback;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public RouteCallback getCallback() {
            return callback;
        }

        @Override
        public String toString() {
            return String.format("[%6s] %s", method, path);
        }
    }

    /**
     * A node of the routing graph
     */
    private static class RouteNode {

        private static final String ID_ROOT = "_root_";

        public static RouteNode root() {
            return new RouteNode(ID_ROOT, false);
        };

        public static RouteNode childNode(String nodeId, boolean parameter) {
            return new RouteNode(nodeId, parameter);
        };

        private final String nodeId;
        private final Map<String, RouteNode> children = Maps.newHashMap();
        private final EnumMap<HttpMethod, RouteInfo> routeInfoMap = Maps.newEnumMap(HttpMethod.class);
        private final Optional<String> paramName;
        private final boolean parameter;

        private RouteNode(String nodeId, boolean parameter) {
            Matcher m = REGEX_PARAM.matcher(nodeId);

            this.nodeId = nodeId;
            this.parameter = parameter;
            this.paramName = Optional.fromNullable((m.matches()) ? m.group(1) : null);
        }

        public Optional<RouteInfo> getRouteInfo(HttpMethod method) {
            return Optional.fromNullable(routeInfoMap.get(method));
        }

        /**
         * Set the route info to the current node
         *
         * @param routeInfo
         * @throws DuplicateRouteException if the route already exists
         */
        public void setRouteInfo(RouteInfo routeInfo) {
            HttpMethod method = routeInfo.getMethod();
            Optional<RouteInfo> r = getRouteInfo(method);
            if (r.isPresent()) {
                throw new DuplicateRouteException(routeInfo);
            } else {
                routeInfoMap.put(method, routeInfo);
            }
        }

        public Optional<RouteNode> findNode(String segment) {
            RouteNode child = children.get(segment);
            return Optional.fromNullable(child);
        }

        public RouteNode findOrCreateNode(String nodeId, boolean parameter) {
            Optional<RouteNode> child = findNode(nodeId);
            if (!child.isPresent()) {
                RouteNode childNode = RouteNode.childNode(nodeId, parameter);
                children.put(nodeId, childNode);
                child = Optional.of(childNode);
            }
            return child.get();
        }

        public String getNodeId() {
            return nodeId;
        }

        public boolean isParameter() {
            return parameter;
        }

        public Optional<String> getParamName() {
            return paramName;
        }
    }

}
