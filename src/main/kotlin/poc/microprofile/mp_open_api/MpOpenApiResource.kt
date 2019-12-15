package poc.microprofile.mp_open_api

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped
import javax.json.bind.annotation.JsonbProperty
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * To show how to annotate the Open API annotation
 */
@ApplicationScoped
@Path("openapi")
class MpOpenApiResource {
    @Path("do-something")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "retrieve-something", summary = "This API is going to retrieve something")
    @APIResponses(
        APIResponse(
            description = "expected response",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = MpOpenApiResponseModel::class)
                )
            ]
        ),
        APIResponse(responseCode = "400", description = "You should provide enough information to me")
    )
    fun createSomething(
        @RequestBody(
            description = "Payload for do something",
            required = true,
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = MpOpenApiRequestModel::class)
                )
            ]
        )
        requestModel: MpOpenApiRequestModel
    ): Response = Response.ok(
        MpOpenApiResponseModel(
            id = requestModel.id,
            date = requestModel.date,
            message = "You get my response!"
        )
    ).build()
}

@Schema(name = "MyRequestModel", description = "POJO that represents a request")
data class MpOpenApiRequestModel(
    @field:Schema(required = true, name = "request-id")
    @field:JsonbProperty("request-id")
    val id: String,

    @field:Schema(required = true, description = "ISO-8601 date")
    val date: LocalDate
)

@Schema(name = "MyResponseModel", description = "POJO that represents a response")
data class MpOpenApiResponseModel(
    @field:Schema(required = true, name = "response-id")
    @field:JsonbProperty("response-id")
    val id: String,

    @field:Schema(required = true)
    val message: String,

    @field:Schema(required = true, description = "ISO-8601 date")
    val date: LocalDate
)
