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

package restelio.support;

import restelio.Restelio.HttpMethod;
import restelio.Restelio.HttpStatus;

public class RequestContext {

    private final RestelioRequest request;
    private final RestelioResponse response;

    public RequestContext(RestelioRequest request, RestelioResponse response) {
        this.request = request;
        this.response = response;
    }

    public RestelioRequest getRequest() {
        return request;
    }

    public RestelioResponse getResponse() {
        return response;
    }

    public void sendError(HttpStatus status) {
        response.sendError(status);
    }

    public HttpMethod getMethod() {
        return request.getMethod();
    }

    public String getPath() {
        return request.getPath();
    }

}
