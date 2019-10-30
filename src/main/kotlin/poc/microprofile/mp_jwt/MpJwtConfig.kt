package poc.microprofile.mp_jwt

import org.eclipse.microprofile.auth.LoginConfig
import javax.annotation.security.DeclareRoles
import javax.enterprise.context.ApplicationScoped

@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles("admin-group")
@ApplicationScoped /* ONLY CDI bean will enable the MP-JWT */
class MpJwtConfig {
}