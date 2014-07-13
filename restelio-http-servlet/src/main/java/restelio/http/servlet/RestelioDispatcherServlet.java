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

    private void handleRoute(HttpMethod method, HttpServletRequest request, HttpServletResponse response) {
        routeHandler.handle(
                new HttpServletRequestWrapper(request),
                new HttpServletResponseWrapper(response));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRoute(HttpMethod.GET, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRoute(HttpMethod.POST, request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRoute(HttpMethod.PUT, request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRoute(HttpMethod.DELETE, request, response);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpStatus.METHOD_NOT_ALLOWED.getCode());
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpStatus.METHOD_NOT_ALLOWED.getCode());
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpStatus.METHOD_NOT_ALLOWED.getCode());
    }
}
