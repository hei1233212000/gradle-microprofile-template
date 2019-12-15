package poc.microprofile.mp_health_check

import org.eclipse.microprofile.health.Liveness
import org.eclipse.microprofile.health.Readiness
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@ApplicationScoped
@Path("dynamic-health-checks")
class MpHealthCheckResource @Inject constructor(
    @Liveness private val livenessHealthCheck: DynamicHealthCheck,
    @Readiness private val readinessHealthCheck: DynamicHealthCheck
) {
    @Path("{healthCheckProcedure}")
    @PUT
    fun updateStatus(
        @PathParam("healthCheckProcedure") healthCheckProcedure: HealthCheckProcedure,
        updateStatusRequest: UpdateStatusRequest
    ) {
        when (healthCheckProcedure) {
            HealthCheckProcedure.Liveness -> livenessHealthCheck.healthCheckProcedureStatus =
                updateStatusRequest.status!!
            HealthCheckProcedure.Readiness -> readinessHealthCheck.healthCheckProcedureStatus =
                updateStatusRequest.status!!
        }
    }
}

enum class HealthCheckProcedure {
    Liveness, Readiness
}

class UpdateStatusRequest {
    var status: HealthCheckProcedureStatus? = null
}
