package poc.microprofile.mp_health_check

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse

interface DynamicHealthCheck : HealthCheck {
    var healthCheckProcedureStatus: HealthCheckProcedureStatus

    override fun call(): HealthCheckResponse {
        val builder = HealthCheckResponse.builder().name(healthCheckName())
        return when (healthCheckProcedureStatus) {
            HealthCheckProcedureStatus.Up -> builder.up().build()
            HealthCheckProcedureStatus.Down -> builder.down().build()
            HealthCheckProcedureStatus.Error -> throw IllegalStateException("Something goes wrong")
        }
    }

    fun healthCheckName(): String
}