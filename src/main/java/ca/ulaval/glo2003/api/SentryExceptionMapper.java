package ca.ulaval.glo2003.api;

import io.sentry.Sentry;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ExceptionMapper;

@Provider
public class SentryExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof jakarta.ws.rs.NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (exception instanceof jakarta.ws.rs.ForbiddenException) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (exception instanceof jakarta.ws.rs.BadRequestException) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Sentry.captureException(exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDTO("INTERNAL_ERROR", "Erreur interne trouvée."))
                .build();
    }
}
