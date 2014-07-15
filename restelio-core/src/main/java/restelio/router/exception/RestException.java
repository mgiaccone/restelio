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

package restelio.router.exception;

import restelio.Restelio.HttpStatus;

public class RestException extends RuntimeException {

    private final HttpStatus httpStatus;

    public static void throwNotFound() {
        throw new RestException(HttpStatus.NOT_FOUND);
    }

    public static void throwForbidden() {
        throw new RestException(HttpStatus.FORBIDDEN);
    }

    public static void throwUnauthorized() {
        throw new RestException(HttpStatus.UNAUTHORIZED);
    }

    public RestException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public RestException(Throwable cause) {
        super(cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
