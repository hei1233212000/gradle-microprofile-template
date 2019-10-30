package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class TimeoutExceptionMapper : ExceptionMapper<TimeoutException> {
    override fun toResponse(exception: TimeoutException): Response = Response.ok("timeout").build()
}
