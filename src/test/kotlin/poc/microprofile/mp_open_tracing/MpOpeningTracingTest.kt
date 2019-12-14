package poc.microprofile.mp_open_tracing

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsIterableContaining.hasItems
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpOpeningTracingTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `try to call the opening tracing endpoint`() {
        given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("api/open-tracing/greeting")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`("Hello"))

        given()
            .accept(ContentType.JSON)
        .`when`()
            .get("api/test/open-tracing/spans/")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("eventName", hasItems("OpenTracingResource", "OpenTracerService"))
    }
}