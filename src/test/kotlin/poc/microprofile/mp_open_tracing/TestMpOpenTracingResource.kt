package poc.microprofile.mp_open_tracing

import fish.payara.micro.cdi.Outbound
import fish.payara.notification.eventbus.EventbusMessage
import fish.payara.notification.requesttracing.RequestTraceSpan
import fish.payara.notification.requesttracing.RequestTracingNotificationData
import java.util.concurrent.CopyOnWriteArrayList
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Initialized
import javax.enterprise.event.Observes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@ApplicationScoped
@Path("test/open-tracing")
class TestMpOpenTracingResource {
    private lateinit var traceSpans : MutableList<RequestTraceSpan>

    @PostConstruct
    fun init() {
        traceSpans = CopyOnWriteArrayList()
    }

    @Path("spans/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun findAllTraceSpans(): List<RequestTraceSpan> = traceSpans

    fun observeOutboundRequestTracingNotificationData(@Observes @Outbound event: EventbusMessage) {
        if (event.data is RequestTracingNotificationData) {
            val notificationData = event.data as RequestTracingNotificationData
            val spans: List<RequestTraceSpan> = notificationData.requestTrace.traceSpans
            traceSpans.addAll(spans)
        }
    }

    fun makeBeanToBeCreatedDuringApplicationStartup(@Observes @Initialized(ApplicationScoped::class) anyBean: Any) {
        // do nothing
    }
}