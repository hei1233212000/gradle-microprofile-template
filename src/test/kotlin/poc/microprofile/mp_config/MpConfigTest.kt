package poc.microprofile.mp_config

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpConfigTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `verify configuration could have default value when using @ConfigProperty`() {
        val subPath = "default"
        val expectedBody = "This is the default value set in @ConfigProperty"
        verifyMpConfig(subPath, expectedBody)
    }

    @Test
    @RunAsClient
    fun `verify configuration could have default value when using Config`() {
        val subPath = "fake-config-key"
        val expectedBody = "config[$subPath] is NOT defined"
        verifyMpConfig(subPath, expectedBody)
    }

    @Test
    @RunAsClient
    fun `verify configuration could have set by microprofile-config properties`() {
        val subPath = "test"
        val expectedBody = "This value is set in the microservice-config.properties"
        verifyMpConfig(subPath, expectedBody)
    }

    private fun verifyMpConfig(subPath: String, expectedBody: String) {
        given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("api/configs/$subPath")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(`is`(expectedBody))
    }
}