package poc.microprofile.mp_rest_client

import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended

@ApplicationScoped
@Path("rest-client")
class MpRestClientResource @Inject constructor(
    @RestClient private val externalService: ExternalService
) {
    @GET
    fun callExternalService(@Suspended asyncResponse: AsyncResponse) {
        externalService.fineOne(1).thenApply {result ->
            asyncResponse.resume(result)
        }
    }
}