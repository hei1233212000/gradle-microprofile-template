package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class CircuitBreakerOpenExceptionMapper : ExceptionMapper<CircuitBreakerOpenException> {
    override fun toResponse(exception: CircuitBreakerOpenException): Response =
        Response
            .status(Response.Status.SERVICE_UNAVAILABLE)
            .entity("The circuit is broken")
            .build()
}
