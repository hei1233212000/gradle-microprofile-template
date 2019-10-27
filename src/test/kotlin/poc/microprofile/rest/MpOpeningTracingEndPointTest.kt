package poc.microprofile.rest

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.junit.InSequence
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(Arquillian::class)
class MpOpeningTracingEndPointTest : AbstractEndPointTest() {
    /**
     * TODO: now we can just checking the log to see if the opentracing is working
     */
    @Test
    @RunAsClient
    @InSequence(1)
    fun `try to call the opening tracing endpoint`() {
        RestAssured.given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("api/open-tracing/greeting")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(Is.`is`("Hello"))
    }
}