package poc.microprofile.mp_metrics

import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Initialized
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.ws.rs.Path

@ApplicationScoped
@Path("metrics")
class MpMetricsResource @Inject constructor(
    val mpMetricsService: MpMetricsService
) {
    @PostConstruct
    fun init() {
        mpMetricsService.doSomething()
        mpMetricsService.doSomething()
        mpMetricsService.doAnotherThing()
    }

    fun init(@Observes @Initialized(ApplicationScoped::class) init: Any) {
        // do nothing and this function is just going to make this bean to be initialized during server startup
    }
}