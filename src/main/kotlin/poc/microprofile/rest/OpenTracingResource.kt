package poc.microprofile.rest

import org.eclipse.microprofile.opentracing.Traced
import poc.microprofile.opentracing.OpenTracerService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * To show Microprofile OpenTracing
 */
@ApplicationScoped
@Path("open-tracing")
class OpenTracingResource @Inject constructor(
    val openTracerService: OpenTracerService
) {
    @Path("greeting")
    @GET
    @Traced(operationName = "OpenTracingResource")
    fun greeting(): String {
        return openTracerService.greeting()
    }
}
