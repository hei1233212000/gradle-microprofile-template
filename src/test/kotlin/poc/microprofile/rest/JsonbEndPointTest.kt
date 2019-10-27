package poc.microprofile.rest

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(Arquillian::class)
class JsonbEndPointTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `verify Json-b is supported in Microprofile`() {
        RestAssured.given()
            .accept(ContentType.JSON)
        .`when`()
            .get("api/jsonb")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", Is.`is`(1))
            .body("custom-field", IsEqual.equalTo("any value"))
    }
}
