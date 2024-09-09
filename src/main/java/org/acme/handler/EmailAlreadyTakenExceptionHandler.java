package org.acme.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.user.exception.EmailAlreadyTakenException;

@Provider
public class EmailAlreadyTakenExceptionHandler implements ExceptionMapper<EmailAlreadyTakenException> {

    @Override
    public Response toResponse(EmailAlreadyTakenException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
    }
}
