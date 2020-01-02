package poc.microprofile.mp_health_check.liveness

import org.eclipse.microprofile.health.Liveness
import poc.microprofile.mp_health_check.DynamicHealthCheck
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@Liveness
class DynamicLivenessHealthCheck : DynamicHealthCheck() {
    override fun healthCheckName(): String = "dynamic-liveness-health-check"
}