package poc.microprofile.mp_jwt

import org.eclipse.microprofile.jwt.JsonWebToken
import org.slf4j.Logger
import javax.annotation.security.DeclareRoles
import javax.annotation.security.RolesAllowed
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("jwt")
@ApplicationScoped
/* MP-JWT ONLY work on CDI bean */
@DeclareRoles("user-group") /* to show we can declare @DeclareRoles in different classes */
class MpJwtResource @Inject constructor(
    private val logger: Logger,
    private val jsonWebToken: JsonWebToken
) {
    @GET
    @RolesAllowed("admin-group", "user-group")
    fun protectedApi(): String {
        logger.info("jsonWebToken: {}", jsonWebToken)
        return "success"
    }
}