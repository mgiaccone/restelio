package restelio.router;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.Restelio.HttpMethod;
import restelio.Restelio.HttpStatus;
import restelio.router.RouteRegistry.RouteMatch;
import restelio.router.exception.RestException;
import restelio.support.RequestContext;

public class RouteFilterChain {

    static final Logger log = LoggerFactory.getLogger(RouteFilterChain.class);

            //private List<FilterInfo>

    private final static ThreadLocal<FilterChainExecution> filterChainExecution = new ThreadLocal<FilterChainExecution>();

    void handle(Optional<RouteMatch> match, RequestContext context, HandlerExecutionCallback callback) {
        // Prepare for the execution
        prepareFilterChain(context, callback);

        // Start execution of the chain. Catch and handle any exception thrown
        try {
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

    protected void apply(RequestContext context, RouteFilterChain chain) {
        // run next matching filter
        FilterChainExecution execution = getFilterChainExecution();

    }

    private void prepareFilterChain(RequestContext context, HandlerExecutionCallback callback) {
        FilterChainExecution execution = new FilterChainExecution(context.getPath(), callback);
        filterChainExecution.set(execution);
    }

    private void cleanupFilterChain() {
        filterChainExecution.remove();
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

        private String matchPattern;
        private HttpMethod method;


    }

}
