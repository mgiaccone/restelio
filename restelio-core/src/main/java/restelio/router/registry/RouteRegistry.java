package restelio.router.registry;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.http.HttpMethod;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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
        RouteInfo routeInfo = new RouteInfo(method, path, callback);
        if (isPathValid(path)) {
            RouteInfo result = registerInternal(rootNode, splitPath(path), routeInfo);
            if (log.isInfoEnabled()) {
                log.info(String.format("Registered route %s", result.toString()));
            }
            return result;
        } else {
            throw new RouteRegistrationException("Duplicate path parameter", routeInfo);
        }
    }

    public RouteMatchResult find(HttpMethod method, String path) {
        Optional<RouteMatchResult> result = findInternal(rootNode, method, splitPath(path));
        if (result.isPresent()) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Match %s", result.get().toString()));
            }
            return result.get();
        } else {
            throw new RouteNotFoundException(path);
        }
    }

    public Optional<RouteMatchResult> findInternal(RouteNode parentNode, HttpMethod method, LinkedList<String> path) {
        if (path.size() > 0) {
            String segment = path.pop();

            // Look for segment nodes first
            Optional<RouteNode> child = parentNode.findNode(segment);
            if (child.isPresent()) {
                Optional<RouteMatchResult> result = findInternal(child.get(), method, path);
                if (result.isPresent()) {
                    RouteNode node = child.get();
                    if (node.isParameter()) {
                        result.get().putParameter(node.getParamName().get(), segment);
                    }
                }
                return result;
            }
        } else {
            Optional<RouteInfo> routeInfo = parentNode.getRouteInfo(method);
            if (routeInfo.isPresent()) {
                return Optional.of(new RouteMatchResult(routeInfo));
            }
        }
        return Optional.absent();
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

    private boolean isPathValid(String path) {
        Set<String> params = Sets.newHashSet();
        Matcher m = REGEX_PARAM.matcher(path);
        while (m.find()) {
            String param = m.group(1);
            if (params.contains(param)) {
                return false;
            } else {
                params.add(param);
            }
        }
        return true;
    }

    private LinkedList<String> splitPath(String path) {
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

        private Optional<RouteInfo> routeInfo;
        private Map<String, String> pathParameters;

        private RouteMatchResult(Optional<RouteInfo> routeInfo) {
            this.routeInfo = routeInfo;
            this.pathParameters = Maps.newHashMap();
        }

        public Optional<RouteInfo> getRouteInfo() {
            return routeInfo;
        }

        public Optional<String> getParameter(String name) {
            return Optional.fromNullable(pathParameters.get(name));
        }

        private void putParameter(String name, String value) {
            pathParameters.put(name, value);
        }

        @Override
        public String toString() {
            if (routeInfo.isPresent()) {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (String key : pathParameters.keySet()) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(String.format("%s=%s", key, pathParameters.get(key)));
                    i++;
                }
                return String.format("%s [%s]", routeInfo.get().toString(), sb.toString());
            } else {
                return "Empty RouteInfo...";
            }
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
        private Optional<RouteNode> paramNode;

        private RouteNode(String nodeId, boolean parameter) {
            Matcher m = REGEX_PARAM.matcher(nodeId);

            this.nodeId = nodeId;
            this.parameter = parameter;
            this.paramName = Optional.fromNullable((m.matches()) ? m.group(1) : null);
            this.paramNode = Optional.absent();
        }

        public Optional<RouteInfo> getRouteInfo(HttpMethod method) {
            return Optional.fromNullable(routeInfoMap.get(method));
        }

        /**
         * Set the route info to the current node
         *
         * @param routeInfo
         * @throws RouteRegistrationException if the route already exists
         */
        public void setRouteInfo(RouteInfo routeInfo) {
            HttpMethod method = routeInfo.getMethod();
            Optional<RouteInfo> r = getRouteInfo(method);
            if (r.isPresent()) {
                throw new RouteRegistrationException("Route not found", routeInfo);
            } else {
                routeInfoMap.put(method, routeInfo);
            }
        }

        public Optional<RouteNode> findNode(String segment) {
            RouteNode child = children.get(segment);
            if (child == null && paramNode.isPresent()) {
                // Not a segment, try to see if it is a parameter
                child = paramNode.get();
            }
            return Optional.fromNullable(child);
        }

        public RouteNode findOrCreateNode(String nodeId, boolean parameter) {
            Optional<RouteNode> child = findNode(nodeId);
            if (!child.isPresent()) {
                RouteNode childNode = RouteNode.childNode(nodeId, parameter);
                child = Optional.of(childNode);
                if (!parameter) {
                    // Add segment to path
                    children.put(nodeId, childNode);
                } else {
                    // Set the parameter node for this segment in path
                    paramNode = Optional.fromNullable(childNode);
                }
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

        public Optional<RouteNode> getParamNode() {
            return paramNode;
        }

    }

}
