package poc.microprofile.mp_jwt

import io.restassured.RestAssured.given
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest
import poc.microprofile.test.JwtGenerator

@RunWith(Arquillian::class)
internal class MpJwtTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `should not able to call the API if no token is provided`() {
        given()
        .`when`()
            .get("api/jwt")
        .then()
            .statusCode(401)
    }

    @Test
    @RunAsClient
    fun `should not able to call the API if invalid token is provided`() {
        val accessToken = JwtGenerator.generateJWT("fake-group")

        given()
            .auth()
            .oauth2(accessToken)
        .`when`()
            .get("api/jwt")
        .then()
            .statusCode(403)
    }

    @Test
    @RunAsClient
    fun `should able to call the API if valid token is provided`() {
        val accessToken = JwtGenerator.generateJWT("admin-group")

        given()
            .auth()
            .oauth2(accessToken)
        .`when`()
            .get("api/jwt")
        .then()
            .statusCode(200)
            .body(`is`("success"))
    }
}