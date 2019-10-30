package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class BulkheadExceptionMapper : ExceptionMapper<BulkheadException> {
    override fun toResponse(exception: BulkheadException?): Response =
        Response.status(Response.Status.SERVICE_UNAVAILABLE)
            .entity("BulkheadException is thrown")
            .build()
}
