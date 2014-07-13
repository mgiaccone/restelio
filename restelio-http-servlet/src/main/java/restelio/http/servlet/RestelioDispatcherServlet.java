package restelio.http.servlet;

import restelio.Restelio;
import restelio.Restelio.HttpMethod;
import restelio.Restelio.HttpStatus;
import restelio.http.servlet.support.HttpServletRequestWrapper;
import restelio.http.servlet.support.HttpServletResponseWrapper;
import restelio.router.RouteHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestelioDispatcherServlet extends HttpServlet {

    private Restelio restelio;
    private RouteHandler routeHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.restelio = Restelio.bootstrap();
        this.routeHandler = restelio.getRouteHandler();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (HttpMethod.GET.name().equals(method)) {
            handleRoute(HttpMethod.GET, request, response);
        } else if (HttpMethod.POST.name().equals(method)) {
            handleRoute(HttpMethod.POST, request, response);
        } else if (HttpMethod.PUT.name().equals(method)) {
            handleRoute(HttpMethod.PUT, request, response);
        } else if (HttpMethod.DELETE.name().equals(method)) {
            handleRoute(HttpMethod.DELETE, request, response);
        } else {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.getCode());
        }
    }

    /**
     * Transfer the request to the route handler
     *
     * @param method HTTP Method
     * @param request HTTP Servlet Request
     * @param response HTTP Servlet Response
     */
    private void handleRoute(HttpMethod method, HttpServletRequest request, HttpServletResponse response) {
        routeHandler.handle(new HttpServletRequestWrapper(request), new HttpServletResponseWrapper(response));
    }

}
