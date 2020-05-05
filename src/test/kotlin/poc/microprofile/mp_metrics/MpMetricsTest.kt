package poc.microprofile.mp_metrics

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matcher
import org.hamcrest.collection.IsMapContaining.hasKey
import org.hamcrest.core.Is.`is`
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.junit.runner.RunWith
import poc.microprofile.test.AbstractEndPointTest

@RunWith(Arquillian::class)
internal class MpMetricsTest : AbstractEndPointTest() {
    @Test
    @RunAsClient
    fun `should able to get the metrics of the all scopes`() {
        verifyMetrics(
            "$", hasKey("base"),
            "$", hasKey("vendor"),
            "$", hasKey("application")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Counter)`() {
        verifyMetrics(
            "application.do-something-count;tag1=value1", `is`(2),
            "application.do-another-thing-count;tag2=value2", `is`(1)
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Timer)`() {
        verifyMetrics(
            "application.do-another-thing-timer.count", `is`(1),
            "application.do-another-thing-timer", hasKey("max"),
            "application.do-another-thing-timer", hasKey("min"),
            "application.do-another-thing-timer", hasKey("mean"),
            "application.do-another-thing-timer", hasKey("p50"),
            "application.do-another-thing-timer", hasKey("p75"),
            "application.do-another-thing-timer", hasKey("p95"),
            "application.do-another-thing-timer", hasKey("p98"),
            "application.do-another-thing-timer", hasKey("p99"),
            "application.do-another-thing-timer", hasKey("p999"),
            "application.do-another-thing-timer", hasKey("oneMinRate"),
            "application.do-another-thing-timer", hasKey("fiveMinRate"),
            "application.do-another-thing-timer", hasKey("fifteenMinRate"),
            "application.do-another-thing-timer", hasKey("meanRate"),
            "application.do-another-thing-timer", hasKey("stddev")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Meter)`() {
        // it is a complex type of Counter
        verifyMetrics(
            "application.my-meter",  hasKey("count"),
            "application.my-meter",  hasKey("oneMinRate"),
            "application.my-meter",  hasKey("fiveMinRate"),
            "application.my-meter",  hasKey("fifteenMinRate"),
            "application.my-meter",  hasKey("meanRate")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Gauge)`() {
        // A gauge is the simplest metric type that just returns a value
        verifyMetrics(
            "application.my-gauge",  `is`(-99)
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (ConcurrentGauge)`() {
        // A gauge is the simplest metric type that just returns a value
        verifyMetrics(
            "application.my-concurrent-gauge",  hasKey("min"),
            "application.my-concurrent-gauge",  hasKey("max"),
            "application.my-concurrent-gauge",  hasKey("current")
        )
    }

    private fun verifyMetrics(path: String, matcher: Matcher<*>, vararg additionalKeyMatcherPairs: Any) {
        given()
            .accept(ContentType.JSON)
        .`when`()
            .get("${baseUrlWithoutContext()}/metrics")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(path, matcher, *additionalKeyMatcherPairs)
    }
}