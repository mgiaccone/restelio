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
