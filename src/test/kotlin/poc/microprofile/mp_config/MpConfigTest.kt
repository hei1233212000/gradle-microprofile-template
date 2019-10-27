package poc.microprofile.mp_config

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEnd2EndTest

@RunWith(Arquillian::class)
class MpConfigTest : AbstractEnd2EndTest() {
    @Test
    @RunAsClient
    fun `verify configuration could have default value when using @ConfigProperty`() {
        RestAssured.given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("${configResourceUrl()}/default")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(Is.`is`("This is the default value set in @ConfigProperty"))
    }

    @Test
    @RunAsClient
    fun `verify configuration could have default value when using Config`() {
        val configKey = "fake-config-key"

        RestAssured.given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("${configResourceUrl()}/$configKey")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(Is.`is`("config[$configKey] is NOT defined"))
    }

    @Test
    @RunAsClient
    fun `verify configuration could have set by microprofile-config properties`() {
        RestAssured.given()
            .accept(ContentType.TEXT)
        .`when`()
            .get("${configResourceUrl()}/test")
        .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(Is.`is`("This value is set in the microservice-config.properties"))
    }

    private fun configResourceUrl() = "api/configs"
}