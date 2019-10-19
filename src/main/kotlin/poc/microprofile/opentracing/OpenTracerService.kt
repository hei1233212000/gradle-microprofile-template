package poc.microprofile.opentracing

import io.opentracing.Tracer
import org.eclipse.microprofile.opentracing.Traced
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class OpenTracerService @Inject constructor(
    val tracer: Tracer
) {
    @Traced(operationName = "OpenTracerService")
    fun greeting(): String {
        // Tag will be shown in the log
        tracer.activeSpan().setTag("tag_key_1", "this is a value of tag_key_1")
        return "Hello"
    }
}
