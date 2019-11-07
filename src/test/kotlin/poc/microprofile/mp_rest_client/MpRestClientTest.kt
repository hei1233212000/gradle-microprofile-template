package poc.microprofile.mp_rest_client

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpRestClientTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `should able to use the rest client to call the external service`() {
        given()
            .accept(ContentType.JSON)
        .`when`()
            .get("api/rest-client")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(
                "userId", `is`(1),
                "id", `is`(1),
                "title", `is`("delectus aut autem"),
                "completed", `is`(false)
            )
    }
}
