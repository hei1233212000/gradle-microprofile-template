package poc.microprofile.mp_health_check

import org.eclipse.microprofile.health.Liveness
import org.eclipse.microprofile.health.Readiness
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path

@ApplicationScoped
@Path("health-check")
class MpHealthCheckResource @Inject constructor(
    @Liveness private val livenessHealthCheck: DynamicHealthCheck,
    @Readiness private val readinessHealthCheck: DynamicHealthCheck
) {
    @POST
    fun checkStatus(updateStatusRequest: UpdateStatusRequest) {
        when (updateStatusRequest.healthCheckProcedure) {
            HealthCheckProcedure.Liveness -> livenessHealthCheck.healthCheckProcedureStatus =
                updateStatusRequest.newStatus!!
            HealthCheckProcedure.Readiness -> readinessHealthCheck.healthCheckProcedureStatus =
                updateStatusRequest.newStatus!!
        }
    }
}

enum class HealthCheckProcedure {
    Liveness, Readiness
}

class UpdateStatusRequest {
    var healthCheckProcedure: HealthCheckProcedure? = null
    var newStatus: HealthCheckProcedureStatus? = null
}
