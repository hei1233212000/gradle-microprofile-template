package poc.microprofile.mp_open_tracing

import io.opentracing.Tracer
import org.eclipse.microprofile.opentracing.Traced
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class MpOpenTracerService @Inject constructor(
    val tracer: Tracer
) {
    @Traced(operationName = "OpenTracerService")
    fun greeting(): String {
        // Tag will be shown in the log
        tracer.activeSpan().setTag("tag_key_1", "this is a value of tag_key_1")
        return "Hello"
    }
}
