package poc.microprofile.mp_open_api

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsIterableContaining.hasItems
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.test.api.ArquillianResource
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest
import java.net.URL

@RunWith(Arquillian::class)
class MpOpenApiTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `try to call the opening tracing endpoint`(@ArquillianResource url: URL) {
        val baseUrl = "${url.protocol}://${url.host}:${url.port}"
        RestAssured.given()
                .accept(ContentType.JSON)
            .`when`()
                .get("${baseUrl}/openapi?format=json")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(
                    "openapi", `is`("3.0.0"),
                    "info.title", `is`("A Test Application"),
                    "info.version", `is`("1.0.0-SNAPSHOT"),
                    "servers.url", hasItems("${baseUrl}/${url.path.removePrefix("/").removeSuffix("/")}"),
                    "paths./api/openapi/do-something.post.operationId", `is`("retrieve-something"),
                    "paths./api/openapi/do-something.post.summary", `is`("This API is going to retrieve something"),
                    "paths./api/openapi/do-something.post.requestBody.required", `is`(true),
                    "paths./api/openapi/do-something.post.requestBody.description", `is`("Payload for do something"),
                    //"paths./api/openapi/do-something.post.requestBody.content.application/json.schema.#ref", `is`("#/components/schemas/MyRequestModel"),
                    "paths./api/openapi/do-something.post.responses.400.description", `is`("You should provide enough information to me"),
                    "paths./api/openapi/do-something.post.responses.default.description", `is`("expected response"),
                    "components.schemas.MyRequestModel.description", `is`("POJO that represents a request"),
                    "components.schemas.MyRequestModel.properties.request-id.type", `is`("string"),
                    "components.schemas.MyRequestModel.properties.date.type", `is`("object"),
                    "components.schemas.MyResponseModel.description", `is`("POJO that represents a response")
                )
    }
}