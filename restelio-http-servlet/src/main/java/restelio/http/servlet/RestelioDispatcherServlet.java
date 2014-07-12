package restelio.http.servlet;

import restelio.http.HttpStatus;
import restelio.router.RestelioRouter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestelioDispatcherServlet extends HttpServlet {

    private RestelioRouter router;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        router = RestelioRouter.configure();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(request, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(request, resp);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(request, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(request, resp);
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
