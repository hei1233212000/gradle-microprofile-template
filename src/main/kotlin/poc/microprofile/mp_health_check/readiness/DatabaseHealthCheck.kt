package poc.microprofile.mp_health_check.readiness

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Readiness
import javax.enterprise.context.ApplicationScoped

/**
 * It represent the current health of the application.
 * If the readiness is down, wait for it to become healthy
 *
 * e.g: DB connection
 */
@ApplicationScoped
@Readiness
class DatabaseHealthCheck : HealthCheck {
    override fun call(): HealthCheckResponse {
        return HealthCheckResponse
            .builder()
            .name("database-health-check")
            .up()
            .withData("db-url", "fake-url")
            .build()
    }
}