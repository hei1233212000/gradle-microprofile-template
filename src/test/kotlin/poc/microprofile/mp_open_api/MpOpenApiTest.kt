package poc.microprofile.mp_open_api

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsIterableContaining.hasItems
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.test.api.ArquillianResource
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEnd2EndTest
import java.net.URL

@RunWith(Arquillian::class)
class MpOpenApiTest : AbstractEnd2EndTest() {
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
                    "openapi", equalTo("3.0.0"),
                    "info.title", equalTo("A Test Application"),
                    "info.version", equalTo("1.0.0-SNAPSHOT"),
                    "servers.url", hasItems("${baseUrl}/${url.path.removePrefix("/").removeSuffix("/")}"),
                    "paths./api/openapi/do-something.post.operationId", equalTo("retrieve-something"),
                    "paths./api/openapi/do-something.post.summary", equalTo("This API is going to retrieve something"),
                    "paths./api/openapi/do-something.post.requestBody.required", equalTo(true),
                    "paths./api/openapi/do-something.post.requestBody.description", equalTo("Payload for do something"),
                    //"paths./api/openapi/do-something.post.requestBody.content.application/json.schema.#ref", equalTo("#/components/schemas/MyRequestModel"),
                    "paths./api/openapi/do-something.post.responses.400.description", equalTo("You should provide enough information to me"),
                    "paths./api/openapi/do-something.post.responses.default.description", equalTo("expected response"),
                    "components.schemas.MyRequestModel.description", equalTo("POJO that represents a request"),
                    "components.schemas.MyRequestModel.properties.request-id.type", equalTo("string"),
                    "components.schemas.MyRequestModel.properties.date.type", equalTo("object"),
                    "components.schemas.MyResponseModel.description", equalTo("POJO that represents a response")
                )
    }
}