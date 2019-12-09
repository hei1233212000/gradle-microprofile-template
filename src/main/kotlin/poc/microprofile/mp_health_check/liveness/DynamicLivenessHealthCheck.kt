package poc.microprofile.mp_health_check.liveness

import org.eclipse.microprofile.health.Liveness
import poc.microprofile.mp_health_check.DynamicHealthCheck
import poc.microprofile.mp_health_check.HealthCheckProcedureStatus
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@Liveness
class DynamicLivenessHealthCheck(override var healthCheckProcedureStatus: HealthCheckProcedureStatus) : DynamicHealthCheck {
    override fun healthCheckName(): String = "dynamic-liveness-health-check"
}