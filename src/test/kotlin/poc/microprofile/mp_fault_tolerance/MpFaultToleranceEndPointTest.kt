package poc.microprofile.mp_fault_tolerance

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.amshove.kluent.`should be equal to`
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.client.ClientBuilder

@RunWith(Arquillian::class)
class MpFaultToleranceEndPointTest : AbstractEndPointTest() {
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
        // first call will return the normal response
        val httpClient = ClientBuilder.newClient()
        val firstCallResult = httpClient
            .target("${uri}api/fault-tolerance/bulkhead?id=1")
            .request()
            .async()
            .get(String::class.java)
        Thread.sleep(500)

        // second call will trigger the bulkhead feature
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/bulkhead?id=2")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("This is a bulkhead fallback function: 2"))

        firstCallResult.get() `should be equal to` "Done: 1"
        httpClient.close()
    }

    @Test
    @RunAsClient
    fun `should break the circuit when fail too many times`() {
        // first call will return the normal response
        val httpClient = ClientBuilder.newClient()
        assertThrows<InternalServerErrorException> {
            httpClient
                .target("${uri}api/fault-tolerance/circuit-breaker")
                .request()
                .get(String::class.java)
        }
        httpClient.close()

        // second call will trigger the circuit breaker feature
        RestAssured.given()
        .`when`()
            .get("api/fault-tolerance/circuit-breaker")
        .then()
            .statusCode(503)
            .body(`is`("The circuit is broken"))
    }
}