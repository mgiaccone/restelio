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
