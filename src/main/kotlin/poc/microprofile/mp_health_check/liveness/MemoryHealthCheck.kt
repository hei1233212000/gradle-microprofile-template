package poc.microprofile.mp_health_check.liveness

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import javax.enterprise.context.ApplicationScoped

/**
 * If Liveness is down, the monitor should restart the container
 *
 * e.g: memory and CPU usage is normal
 */
@ApplicationScoped
@Liveness
class MemoryHealthCheck : HealthCheck {
    override fun call(): HealthCheckResponse {
        return HealthCheckResponse
            .builder()
            .name("memory-health-check")
            .up()
            .withData("free-memory", Runtime.getRuntime().freeMemory())
            .build()
    }
}