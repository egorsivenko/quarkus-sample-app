package org.acme.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.user.exception.IncorrectPasswordException;

@Provider
public class IncorrectPasswordExceptionHandler implements ExceptionMapper<IncorrectPasswordException> {

    @Override
    public Response toResponse(IncorrectPasswordException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
    }
}
