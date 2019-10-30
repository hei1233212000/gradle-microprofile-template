package poc.microprofile.mp_rest_client

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.concurrent.CompletionStage
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

data class Todo(
    var id: Int? = null,
    var userId: Int? = null,
    var title: String? = null,
    var completed: Boolean? = null
)

// It is not the best way to call the real external API as it may be broken in the future
@RegisterRestClient
@Path("/todos")
interface ExternalService {
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun fineOne(@PathParam("id") id: Int): CompletionStage<Todo>
}