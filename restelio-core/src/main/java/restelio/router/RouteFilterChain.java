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

package restelio.router;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.Restelio.HttpStatus;
import restelio.router.RouteRegistry.RouteMatch;
import restelio.router.exception.RestException;
import restelio.support.RequestContext;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class RouteFilterChain {

    static final Logger log = LoggerFactory.getLogger(RouteFilterChain.class);

    private static final String PATTERN_MATCH_ALL = "/*";

    private static final ThreadLocal<FilterChainExecution> filterChainExecution = new ThreadLocal<FilterChainExecution>();

    private List<FilterInfo> filters;

    public RouteFilterChain() {
        filters = Lists.newArrayList();
    }

    final void init() {
        if (log.isInfoEnabled()) {
            log.info("Initializing route filter chain");
        }

        registerFilter(PATTERN_MATCH_ALL, Integer.MIN_VALUE, new SecurityFilter());

        if (filters.size() > 0) {
            // Make sure the filter list is ordered
            Function<FilterInfo, Integer> sortFunction = new Function<FilterInfo, Integer>() {
                public Integer apply(FilterInfo filterInfo) {
                    return filterInfo.order;
                }
            };

            // Sort the list by filter order
            filters = Ordering.natural()
                    .onResultOf(sortFunction)
                    .immutableSortedCopy(filters);
        }
    }

    final void handle(Optional<RouteMatch> match, RequestContext context, HandlerExecutionCallback callback) {
        // Start execution of the chain. Catch and handle any exception thrown
        try {
            // Prepare for the execution
            prepareFilterChain(context, callback);

            // If no match, fail fast without executing any filter
            if (match.isPresent()) {
                apply(context, this);
            } else {
                RestException.throwNotFound();
            }
        } catch (RestException e) {
            if (log.isErrorEnabled()) {
                log.error("Error while processing the filter chain", e);
            }
            context.sendError(e.getHttpStatus());
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Ops.. I got an unwrapped exception while processing the filter chain", e);
            }
            context.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // Make sure we are good boy scouts and we leave everything clean after us
            cleanupFilterChain();
        }
    }

    public final void apply(RequestContext context, RouteFilterChain chain) {
        // run next matching filter
        HttpMethod method = context.getMethod();
        String path = context.getPath();
        FilterChainExecution execution = getFilterChainExecution();

        int lastFilterIndex = execution.getFilterIndex();
        if (lastFilterIndex < filters.size()) {
            // Execute the next filter
            for (int i = lastFilterIndex; i < filters.size(); i++) {
                FilterInfo filterInfo = filters.get(i);
                if (filterInfo.matches(method, path)) {
                    RouteFilter filter = filterInfo.getInstance();
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Applying filter [%s] to path [%s %s]",
                                        filterInfo.getFilterClassName(), method, path)
                        );
                    }

                    execution.incrementFilterIndex();
                    filter.apply(context, chain);

                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Finished applying filter [%s] to path [%s %s]",
                                        filterInfo.getFilterClassName(), method, path)
                        );
                    }

                    break;
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Skipping filter [%s] for path [%s %s]",
                                        filterInfo.getFilterClassName(),
                                        method, path)
                        );
                    }
                    execution.incrementFilterIndex();
                }
            }
        } else {
            // Execute the route handler
            execution.callback.executeHandler();
        }
    }

    private void prepareFilterChain(RequestContext context, HandlerExecutionCallback callback) {
        FilterChainExecution execution = new FilterChainExecution(context.getPath(), callback);
        filterChainExecution.set(execution);
    }

    private void cleanupFilterChain() {
        filterChainExecution.remove();
    }


    public void registerFilter(String pattern, int order, RouteFilter instance) {
        registerFilter(null, pattern, order, instance);
    }

    public void registerFilter(HttpMethod[] methods, String pattern, int order, RouteFilter instance) {
        // TODO: compile pattern
        FilterInfo filterInfo = new FilterInfo(methods, null, order, instance);
        filters.add(filterInfo);
    }

    private FilterChainExecution getFilterChainExecution() {
        return filterChainExecution.get();
    }

    /**
     * Internal callback to allow the execution of the handler within the chain
     */
    static interface HandlerExecutionCallback {
        void executeHandler() throws RestException;
    }

    /**
     * Class containing the filter chain execution status for the current thread
     */
    private static class FilterChainExecution {

        private int filterIndex;
        private String path;
        private HandlerExecutionCallback callback;

        private FilterChainExecution(String path, HandlerExecutionCallback callback) {
            this.path = path;
            this.callback = callback;
        }

        public void incrementFilterIndex() {
            filterIndex++;
        }

        public int getFilterIndex() {
            return filterIndex;
        }
    }

    /**
     * Filter metadata
     */
    public static class FilterInfo {

        static final Logger log = LoggerFactory.getLogger(FilterInfo.class);

        private final Set<HttpMethod> methods;
        private final Pattern pattern;
        private final int order;
        private final RouteFilter instance;

        public FilterInfo(HttpMethod[] methods, Pattern pattern, int order, RouteFilter instance) {
            this.methods = Sets.newHashSet((methods == null) ? new HttpMethod[] {} : methods);
            this.pattern = pattern;
            this.order = order;
            this.instance = instance;
        }

        public boolean matches(HttpMethod method, String path) {
            // TODO: add glob path matching
            return (methods.size() == 0 || methods.contains(method));
        }

        public String getFilterClassName() {
            return instance.getClass().getSimpleName();
        }

        public Pattern getPattern() {
            return pattern;
        }

        public int getOrder() {
            return order;
        }

        public RouteFilter getInstance() {
            return instance;
        }
    }

    /**
     * Security filter implementation
     */
    static class SecurityFilter implements RouteFilter {

        static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

        @Override
        public void apply(RequestContext context, RouteFilterChain chain) throws RestException {
            chain.apply(context, chain);
        }

    }

}
