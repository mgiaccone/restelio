package restelio.support;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// FIXME: Convert code to use Guava and Optional
public class Url {

    static final Logger log = LoggerFactory.getLogger(Url.class);

    private static final int DEFAULT_HTTP_PORT  = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final String DEFAULT_PATH    = "/";

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RestelioRequest request) {
        return new Builder().protocol(request.getScheme())
                .host(request.getHostname())
                .port(request.getPort())
                .path(request.getPath())
                .queryString(request.getQueryString());
    }

    public static Builder builder(String url) {
        return new Builder(url);
    }

    public static Builder builder(String protocol, String host, int port, String path, String queryString) {
        return new Builder().protocol(protocol)
                .host(host)
                .port(port)
                .path(path)
                .queryString(Optional.fromNullable(queryString));
    }

    private boolean queryAsFragment;
    private String protocol;
    private String host;
    private String path;
    private Optional<String> fragment;
    private Map<String, String> queryParams;
    private int port = DEFAULT_HTTP_PORT;

    private Url(String protocol, String host, String path) {
        this(protocol, host, DEFAULT_HTTP_PORT, path);
    }

    private Url(String protocol, String host, int port) {
        this(protocol, host, port, null);
    }

    private Url(String protocol, String host, int port, String path) {
        this.protocol = protocol;
        this.host = host;
        this.port = (port > 0 && port != DEFAULT_HTTP_PORT && port != DEFAULT_HTTPS_PORT) ? port : 0;
        this.path = path;
    }

    private Url() {

    }

    public String getProtocol() {
        return protocol;
    }

    private void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    public Optional<String> getFragment() {
        return fragment;
    }

    private void setFragment(Optional<String> fragment) {
        this.fragment = fragment;
    }

    private boolean isQueryAsFragment() {
        return queryAsFragment;
    }

    private void setQueryAsFragment(boolean queryAsFragment) {
        this.queryAsFragment = queryAsFragment;
    }

//    public Map<String, String> getQueryParams() {
//        return queryParams;
//    }
//
    private void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Optional<String> getParameter(String name) {
        String value = (queryParams != null) ? queryParams.get(name) : null;
        return Optional.fromNullable(value);
    }

    public String getQueryString() {
        final StringBuilder sb = new StringBuilder();
        int paramCount = 0;
        if (queryParams != null && queryParams.size() > 0) {
            for (String param : queryParams.keySet()) {
                if (paramCount > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", param, encodeForUrl(queryParams.get(param))));
                paramCount++;
            }
        }
        return sb.toString();
    }

    private String encodeForUrl(String value) {
        String encodedValue;
        try {
            encodedValue = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return encodedValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s://%s", protocol, host));

        if (port != DEFAULT_HTTP_PORT && port != DEFAULT_HTTPS_PORT) {
            sb.append(":");
            sb.append(port);
        }

        if (path != null && path.trim().length() > 0) {
            sb.append(path);
        }

        String queryString = getQueryString();
        if (queryString != null && queryString.trim().length() > 0) {
            sb.append((queryAsFragment) ? "#" : "?");
            sb.append(queryString);
        }

        if (!queryAsFragment) {
            if (fragment.isPresent() && fragment.get().trim().length() > 0) {
                sb.append("#");
                sb.append(fragment.get());
            }
        }
        return sb.toString();
    }

    /**
     * Builder for OAuthUrl
     */
    public static class Builder {

        /*
         * REGEX_URL:
         *
         * ^(http[s]?)(?::\/\/)([^:\/\s#\?$]+)(?:\:([0-9]+))?(\/[^\?#\s]*)?(?:\?([^#\s]+))?(?:#([^\s]+))?$
         *
         * group 1 -> schema
         * group 2 -> host
         * group 3 -> port (optional)
         * group 4 -> context path (optional)
         * group 5 -> query (optional)
         * group 6 -> fragment (optional)
         *
         * Tested samples:
         *
         * http://www.example.com:8080/some/path/context?p1=1&p2=2#fragment
         * http://www.example.com:8080/some/path/context?p1=1&p2=2
         * http://www.example.com/some/path/context?p1=1&p2=2
         * https://www.example.com/some/path/context?p1=1&p2=2
         * https://www.example.com?p1=1&p2=2
         * https://www.example.com#fragment
         * https://www.example.com
         * https://www.example.com/?p1=1&p2=2
         * https://www.example.com/#fragment
         * https://www.example.com/
         * http://www.example.com:8080/
         */
        private static final Pattern REGEX_URL   = Pattern.compile(
                "^(http[s]?)(?:://)([^:/\\s#\\?$]+)(?::([0-9]+))?(/[^\\?#\\s]*)?(?:\\?([^#\\s]+))?(?:#([^\\s]+))?$",
                Pattern.DOTALL | Pattern.MULTILINE);

        private static final Pattern REGEX_QUERY = Pattern.compile("(?:\\&|\\?)?(?:([^\\=\\&#]+)\\=([^\\=\\&#]+))");

        private Url url;
        private Map<String, String> queryParams;

        private Builder() {
            url = new Url();
        }

//        private Builder(RestelioRequest request) {
//            this(String.format("%s%s", request.getBaseUri(), request.getRestxUri()));
//        }

        private Builder(String url) {
            fromUrl(url);
        }

        private void fromUrl(String url) {
            Matcher m = REGEX_URL.matcher(url);
            if (m.matches()) {
                String portValue = m.group(3);
                int port = 0;
                if (portValue != null) {
                    port = Integer.parseInt(portValue);
                }

                this.url = new Url(m.group(1), m.group(2), port, m.group(4));
                this.url.setFragment(Optional.fromNullable(m.group(6)));

                // Process query string
                String queryString = m.group(5);
                if (queryString != null) {
                    // Process query params (separate OAuth params?)
                    extractParamsFromQueryString(Optional.fromNullable(queryString));
                }
            }
        }

        public Builder protocol(String protocol) {
            url.setProtocol(protocol);
            return this;
        }

        public Builder host(String host) {
            url.setHost(host);
            return this;
        }

        public Builder port(int port) {
            url.setPort(port);
            return this;
        }

        public Builder path(String path) {
            url.setPath(Optional.fromNullable(path).or(DEFAULT_PATH));
            return this;
        }

        public Builder queryString(Optional<String> queryString) {
            extractParamsFromQueryString(queryString);
            return this;
        }

        public Builder param(String name, String value) {
            String decodedValue;
            try {
                decodedValue = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Add it to the Extra parameters collection
            if (queryParams == null) {
                queryParams = new HashMap<String, String>();
                url.setQueryParams(queryParams);
            }
            queryParams.put(name, decodedValue);

            return this;
        }

        public Builder queryAsFragment(boolean value) {
            url.setQueryAsFragment(value);
            return this;
        }

        public Url build() {
            // FIXME: Verify URL
            return url;
        }

        private void extractParamsFromQueryString(Optional<String> query) {
            if (query.isPresent()) {
                Matcher matcher = REGEX_QUERY.matcher(query.get());
                while (matcher.find()) {
                    String param = matcher.group(1);
                    String value = matcher.group(2);
                    param(param, value);
                }
            }
        }
    }

}
