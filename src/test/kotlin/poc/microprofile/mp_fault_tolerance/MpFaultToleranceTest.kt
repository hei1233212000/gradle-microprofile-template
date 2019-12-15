package poc.microprofile.mp_fault_tolerance

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpFaultToleranceTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `should pass if the process is NOT running too long`() {
        val subPath = "time-out?threshold=500"
        val expectedStatusCode = 200
        val expectedBody = "passed"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    @Test
    @RunAsClient
    fun `should time out if the process is running too long`() {
        val subPath = "time-out?threshold=1500"
        val expectedStatusCode = 200
        val expectedBody = "timeout"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    @Test
    @RunAsClient
    fun `should able to retry`() {
        val subPath = "retry"
        val expectedStatusCode = 200
        val expectedBody = "retried 1 times"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    @Test
    @RunAsClient
    fun `should fallback when service is down`() {
        val subPath = "fallback"
        val expectedStatusCode = 200
        val expectedBody = "This is a fallback function"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    @Test
    @RunAsClient
    fun `should encounter error if the threshold of bullhead is reached`() {
        val subPath = "bulkhead"
        val expectedStatusCode = 503
        val expectedBody = "BulkheadException is thrown"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    @Test
    @RunAsClient
    fun `should break the circuit when fail too many times`() {
        val subPath = "circuit-breaker"
        val expectedStatusCode = 503
        val expectedBody = "The circuit is broken"
        verifyFaultTolerance(subPath, expectedStatusCode, expectedBody)
    }

    private fun verifyFaultTolerance(subPath: String, expectedStatusCode: Int, expectedBody: String) {
        given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("api/fault-tolerance/$subPath")
        .then()
            .statusCode(expectedStatusCode)
            .contentType(ContentType.TEXT)
            .body(`is`(expectedBody))
    }
}