package poc.microprofile.mp_metrics

import org.eclipse.microprofile.metrics.annotation.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MpMetricsService {

    @Counted(name = "do-something-count", absolute = true, tags = ["tag1=value1"])
    fun doSomething() {
        // do nothing
    }

    @Counted(name = "do-another-thing-count", absolute = true, tags = ["tag2=value2"])
    @Timed(name = "do-another-thing-timer", absolute = true)
    fun doAnotherThing() {
        Thread.sleep(1000)
    }

    @Metered(name = "my-meter", absolute = true)
    fun meterFunction() {
        // do nothing
    }

    @Gauge(name = "my-gauge", absolute = true, unit = "random number")
    fun gaugeFunction(): Int = -99

    @ConcurrentGauge(name = "my-concurrent-gauge", absolute = true, unit = "random number")
    fun concurrentGaugeFunction(): Int = (0..2).random()
}