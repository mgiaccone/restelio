package restelio.router;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton graph based route registry
 * @author Matteo Giaccone
 */
public class RouteRegistry {

    static final Logger log = LoggerFactory.getLogger(RouteRegistry.class);

    private static final Pattern REGEX_PARAM    = Pattern.compile("(?:\\{(\\w+)\\})");
    private static final String FORWARD_SLASH   = "/";

    private RouteNode rootNode;

    public RouteRegistry() {
        rootNode = RouteNode.root();
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

    /**
     * Find a route given the HTTP method and a path
     *
     * @param method the route HTTP Method
     * @param path the route path
     * @return A route match if the route was found, throws and exception if the route is not available
     */
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
     * Route metadata
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

        private static final String ID_ROOT     = "__ROOT__";
        private static final String ID_PARAM    = "__PARAM__";

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

            // Not a segment? try to see if it is a parameter
            if (child == null) {
                child = children.get(ID_PARAM);
            }
            return Optional.fromNullable(child);
        }

        public RouteNode findOrCreateNode(String nodeId, boolean parameter) {
            Optional<RouteNode> child = findNode(nodeId);
            if (!child.isPresent()) {
                RouteNode childNode = RouteNode.childNode(nodeId, parameter);
                child = Optional.of(childNode);
                children.put(((parameter) ? ID_PARAM : nodeId), childNode);
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
