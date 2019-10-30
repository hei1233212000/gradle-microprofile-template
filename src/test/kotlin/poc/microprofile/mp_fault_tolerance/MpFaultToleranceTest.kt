package poc.microprofile.mp_fault_tolerance

import io.restassured.RestAssured
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
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/time-out?threshold=500")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("passed"))
    }

    @Test
    @RunAsClient
    fun `should time out if the process is running too long`() {
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/time-out?threshold=1001")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("timeout"))
    }

    @Test
    @RunAsClient
    fun `should able to retry`() {
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/retry")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("retried 1 times"))
    }

    @Test
    @RunAsClient
    fun `should fallback when service is down`() {
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/fallback")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("This is a fallback function"))
    }

    @Test
    @RunAsClient
    fun `should encounter error if the threshold of bullhead is reached`() {
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/bulkhead")
        .then()
            .statusCode(503)
            .contentType(ContentType.TEXT)
            .body(`is`("BulkheadException is thrown"))
    }

    @Test
    @RunAsClient
    fun `should break the circuit when fail too many times`() {
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/circuit-breaker")
        .then()
            .statusCode(503)
            .body(`is`("The circuit is broken"))
    }
}