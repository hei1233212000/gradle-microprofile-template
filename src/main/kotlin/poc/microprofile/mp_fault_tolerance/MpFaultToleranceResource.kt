package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.Fallback
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.faulttolerance.Timeout
import org.slf4j.Logger
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@ApplicationScoped
@Path("fault-tolerance")
class MpFaultToleranceResource @Inject constructor(
    val logger: Logger,
    val mpFaultToleranceService: MpFaultToleranceService
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
    fun bulkhead() {
        try {
            val es = Executors.newCachedThreadPool()
            val f1 = es.submit {
                mpFaultToleranceService.bulkhead(1)
            }
            val f2 = es.submit {
                mpFaultToleranceService.bulkhead(2)
            }
            f1.get()
            f2.get()
        } catch (e: Exception) {
            throw findRootCause(e)
        }
    }

    @Path("circuit-breaker")
    @GET
    fun circuitBreaker() {
        var exception: Exception? = null
        while (exception == null || exception is UnsupportedOperationException) {
            try {
                mpFaultToleranceService.circuitBreaker()
            } catch (e: Exception) {
                exception = e
            }
        }
        throw exception
    }

    private fun fallbackFunction() = "This is a fallback function"

    private fun findRootCause(throwable: Throwable): Throwable {
        return throwable.cause?.let {
            findRootCause(it)
        } ?: throwable
    }
}
