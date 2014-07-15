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

package restelio.http.servlet.support;

import restelio.Restelio.HttpStatus;
import restelio.support.RestelioResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A wrapper for an HTTPServletResponse
 */
public class HttpServletResponseWrapper extends RestelioResponse {

    private HttpServletResponse response;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void sendError(HttpStatus status) {
        try {
            response.sendError(status.getCode());
        } catch (IOException e) {
            // Something bad happened, we can't do much about it at this point
            e.printStackTrace();
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

}
