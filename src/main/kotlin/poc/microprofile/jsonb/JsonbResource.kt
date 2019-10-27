package poc.microprofile.jsonb

import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.json.bind.annotation.JsonbProperty
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * To show Microprofile is supporting Json-b
 */
@ApplicationScoped
@Path("jsonb")
class JsonbResource @Inject constructor(
    val logger: Logger
) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun jsonbModel(): JsonbModel {
        val jsonbModel = JsonbModel(
            id = 1,
            a = "any value"
        )
        logger.info("jsonbModel: {}", jsonbModel)

        return jsonbModel
    }
}

data class JsonbModel(
    val id: Long,
    @field:JsonbProperty("custom-field") val a: String
)
