package poc.microprofile.mp_open_tracing

import org.eclipse.microprofile.opentracing.Traced
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * To show Microprofile OpenTracing
 */
@ApplicationScoped
@Path("open-tracing")
class MpOpenTracingResource @Inject constructor(
    val mpOpenTracerService: MpOpenTracerService
) {
    @Path("greeting")
    @GET
    @Traced(operationName = "OpenTracingResource")
    fun greeting(): String {
        return mpOpenTracerService.greeting()
    }
}
