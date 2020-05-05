package poc.microprofile

import poc.microprofile.Application.Companion.API_PATH
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath(API_PATH)
class Application : Application() {
    companion object {
        const val API_PATH = "api"
    }
}
