package poc.microprofile.mp_config

import org.eclipse.microprofile.config.Config
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@ApplicationScoped
@Path("configs")
class MpConfigResource @Inject constructor(
   val config: Config,

   @ConfigProperty(name = "default", defaultValue = "This is the default value set in @ConfigProperty")
   val default: String
) {
    @Path("default")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun retrieveDefaultConfig(): String = default

    @Path("{key}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun getConfig(@PathParam("key") key: String): String {
        // check the precedence in https://github.com/eclipse/microprofile-config
        return config.getOptionalValue(key /* propertyName */, String::class.java)
            .orElse("config[$key] is NOT defined")
    }
}