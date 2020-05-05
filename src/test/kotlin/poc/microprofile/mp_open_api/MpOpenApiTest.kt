package poc.microprofile.mp_open_api

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsIterableContaining.hasItems
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.test.api.ArquillianResource
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.Application.Companion.API_PATH
import poc.microprofile.test.AbstractEndPointTest
import java.net.URL

@RunWith(Arquillian::class)
internal class MpOpenApiTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `should able to get the MP OpenApi document`(@ArquillianResource url: URL) {
        val baseUrl = baseUrlWithoutContext()
        given()
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
                    "paths./${API_PATH}/openapi/do-something.post.operationId", `is`("retrieve-something"),
                    "paths./${API_PATH}/openapi/do-something.post.summary", `is`("This API is going to retrieve something"),
                    "paths./${API_PATH}/openapi/do-something.post.requestBody.required", `is`(true),
                    "paths./${API_PATH}/openapi/do-something.post.requestBody.description", `is`("Payload for do something"),
                    //"paths./${API_PATH/openapi/do-something.post.requestBody.content.application/json.schema.#ref", `is`("#/components/schemas/MyRequestModel"),
                    "paths./${API_PATH}/openapi/do-something.post.responses.400.description", `is`("You should provide enough information to me"),
                    "paths./${API_PATH}/openapi/do-something.post.responses.default.description", `is`("expected response"),
                    "components.schemas.MyRequestModel.description", `is`("POJO that represents a request"),
                    "components.schemas.MyRequestModel.properties.request-id.type", `is`("string"),
                    "components.schemas.MyRequestModel.properties.date.type", `is`("object"),
                    "components.schemas.MyResponseModel.description", `is`("POJO that represents a response")
                )
    }
}