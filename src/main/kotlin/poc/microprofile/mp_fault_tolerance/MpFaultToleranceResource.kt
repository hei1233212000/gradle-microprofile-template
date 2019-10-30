package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.*
import org.slf4j.Logger
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@ApplicationScoped
@Path("fault-tolerance")
class MpFaultToleranceResource @Inject constructor(
    val logger: Logger
) {
    private val retryCounter = AtomicInteger(0)

    @Path("time-out")
    @GET
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    fun timeout(@QueryParam("threshold") threshold: Long): String {
        Thread.sleep(threshold)
        return "passed"
    }

    @Path("retry")
    @GET
    @Retry(maxRetries = 2, retryOn = [IllegalStateException::class])
    fun retry(): String {
        val numberOfRetry = retryCounter.getAndIncrement()
        logger.info("numberOfRetry: {}", numberOfRetry)
        check(numberOfRetry != 0) { "We have to retry" }
        return "retried 1 times"
    }

    @Path("fallback")
    @GET
    @Fallback(fallbackMethod = "fallbackFunction")
    fun neverSuccessFunction(): String {
        throw UnsupportedOperationException("NOT yet implemented")
    }

    @Path("bulkhead")
    @GET
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "bulkheadFallbackFunction")
    fun bulkhead(@QueryParam("id") id: Long): String {
        logger.info("called: {}", id)
        Thread.sleep(500)
        return "Done: $id"
    }

    @Path("circuit-breaker")
    @GET
    @CircuitBreaker(requestVolumeThreshold = 1)
    fun circuitBreaker() {
        throw UnsupportedOperationException("NOT yet implemented")
    }

    private fun fallbackFunction() = "This is a fallback function"

    private fun bulkheadFallbackFunction(id: Long) = "This is a bulkhead fallback function: $id"
}
