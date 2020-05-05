package poc.microprofile.mp_health_check

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matcher
import org.hamcrest.collection.IsMapContaining.hasEntry
import org.hamcrest.collection.IsMapContaining.hasKey
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpHealthCheckTest : AbstractEndPointTest() {
    private val findHealthCheckTemplate = "checks.find { it['name'] == '%s' }"
    private val findDatabaseHealthCheck = findHealthCheckTemplate.replace("%s", "database-health-check")
    private val findMemoryHealthCheck = findHealthCheckTemplate.replace("%s", "memory-health-check")
    private val findDynamicLivenessHealthCheck = findHealthCheckTemplate.replace("%s", "dynamic-liveness-health-check")
    private val findDynamicReadinessHealthCheck = findHealthCheckTemplate.replace("%s", "dynamic-readiness-health-check")

    @Before
    fun setup() {
        // make sure ALL health check is up
        updateHealthCheckStatus(HealthCheckProcedure.Liveness, HealthCheckProcedureStatus.Up)
        updateHealthCheckStatus(HealthCheckProcedure.Readiness, HealthCheckProcedureStatus.Up)
    }

    @Test
    @RunAsClient
    fun `should able to get the liveness health status`() {
        val expectedStatusCode = 200
        verifyHealthCheck(expectedStatusCode, HealthCheckProcedure.Liveness,
            "status", `is`("UP"),
            "${findMemoryHealthCheck}.status", `is`("UP"),
            "${findMemoryHealthCheck}.data", hasKey("free-memory"),
            "${findDynamicLivenessHealthCheck}.status", `is`("UP")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the readiness health status`() {
        val expectedStatusCode = 200

        verifyHealthCheck(
            expectedStatusCode, HealthCheckProcedure.Readiness,
            "status", `is`("UP"),
            "${findDatabaseHealthCheck}.status", `is`("UP"),
            "${findDatabaseHealthCheck}.data", hasEntry("db-url", "fake-url"),
            "${findDynamicReadinessHealthCheck}.status", `is`("UP")
        )
    }

    @Test
    @RunAsClient
    fun `should see the overall status of liveness to be DOWN if one of the health check is DOWN`() {
        val newStatus = HealthCheckProcedureStatus.Down
        val healthCheckProcedure = HealthCheckProcedure.Liveness
        updateHealthCheckStatus(healthCheckProcedure, newStatus)

        val expectedStatusCode = 503
        verifyHealthCheck(expectedStatusCode, healthCheckProcedure,
            "status", `is`("DOWN"),
            "${findMemoryHealthCheck}.status", `is`("UP"),
            "${findDynamicLivenessHealthCheck}.status", `is`("DOWN")
        )
    }

    @Test
    @RunAsClient
    fun `should see the overall status of readiness to be DOWN if one of the health check is DOWN`() {
        val newStatus = HealthCheckProcedureStatus.Down
        val healthCheckProcedure = HealthCheckProcedure.Readiness
        updateHealthCheckStatus(healthCheckProcedure, newStatus)

        val expectedStatusCode = 503
        verifyHealthCheck(expectedStatusCode, healthCheckProcedure,
            "status", `is`("DOWN"),
            "${findDatabaseHealthCheck}.status", `is`("UP"),
            "${findDynamicReadinessHealthCheck}.status", `is`("DOWN")
        )
    }

    @Test
    @RunAsClient
    fun `should see the overall status of liveness to be DOWN if the producer was not able to process the health check request (error in procedure)`() {
        val newStatus = HealthCheckProcedureStatus.Error
        val healthCheckProcedure = HealthCheckProcedure.Liveness
        updateHealthCheckStatus(healthCheckProcedure, newStatus)

        val expectedStatusCode = 500
        verifyHealthCheck(expectedStatusCode, healthCheckProcedure)
    }

    @Test
    @RunAsClient
    fun `should see the overall status of readiness to be DOWN if the producer was not able to process the health check request (error in procedure)`() {
        val newStatus = HealthCheckProcedureStatus.Error
        val healthCheckProcedure = HealthCheckProcedure.Readiness
        updateHealthCheckStatus(healthCheckProcedure, newStatus)

        val expectedStatusCode = 500
        verifyHealthCheck(expectedStatusCode, healthCheckProcedure)
    }

    private fun updateHealthCheckStatus(
        healthCheckProcedure: HealthCheckProcedure,
        newStatus: HealthCheckProcedureStatus
    ) {
         val payload = "{ \"status\": \"$newStatus\" }"
        given()
            .contentType(ContentType.JSON)
        .`when`()
            .body(payload)
            .put("api/dynamic-health-checks/$healthCheckProcedure")
        .then()
            .statusCode(204)
    }

    private fun verifyHealthCheck(
        expectedStatusCode: Int, healthCheckProcedure: HealthCheckProcedure,
        path: String, matcher: Matcher<*>,
        vararg additionalKeyMatcherPairs: Any
    ): ValidatableResponse {
        val validatableResponse = verifyHealthCheck(expectedStatusCode, healthCheckProcedure)
        return validatableResponse
            .contentType(ContentType.JSON)
            .body(path, matcher, *additionalKeyMatcherPairs)
    }

    private fun verifyHealthCheck(
        expectedStatusCode: Int,
        healthCheckProcedure: HealthCheckProcedure
    ): ValidatableResponse {
        val healthCheckProcedurePath = when (healthCheckProcedure) {
            HealthCheckProcedure.Liveness -> "live"
            HealthCheckProcedure.Readiness -> "ready"
        }
        return given()
            .accept(ContentType.JSON)
        .`when`()
            .get("${baseUrlWithoutContext()}/health/$healthCheckProcedurePath")
        .then()
            .statusCode(expectedStatusCode)
    }

    override fun suspendVerifyingIfServerIsReady(): Boolean = true
}