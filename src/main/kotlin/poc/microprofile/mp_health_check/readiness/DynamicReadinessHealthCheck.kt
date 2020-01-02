package poc.microprofile.mp_health_check.readiness

import org.eclipse.microprofile.health.Readiness
import poc.microprofile.mp_health_check.DynamicHealthCheck
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@Readiness
class DynamicReadinessHealthCheck : DynamicHealthCheck() {
    override fun healthCheckName(): String = "dynamic-readiness-health-check"
}