package poc.microprofile.mp_fault_tolerance

import org.eclipse.microprofile.faulttolerance.Bulkhead
import org.eclipse.microprofile.faulttolerance.CircuitBreaker
import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class MpFaultToleranceService @Inject constructor(
    val logger: Logger
) {
    @Bulkhead(value = 1)
    fun bulkhead(id: Long): String {
        logger.info("called: {}", id)
        Thread.sleep(500)
        return "Done: $id"
    }

    @CircuitBreaker(requestVolumeThreshold = 1)
    fun circuitBreaker() {
        throw UnsupportedOperationException("NOT yet implemented")
    }
}
