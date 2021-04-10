package poc.microprofile.mp_metrics

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
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
            withAppName("application.do-something-count") + ";tag1=value1", `is`(2),
            withAppName("application.do-another-thing-count") + ";tag2=value2", `is`(1)
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Timer)`() {
        verifyMetrics(
            withAppName("application.do-another-thing-timer.count"), `is`(1),
            "application.do-another-thing-timer", hasKeyWithAppName("max"),
            "application.do-another-thing-timer", hasKeyWithAppName("min"),
            "application.do-another-thing-timer", hasKeyWithAppName("mean"),
            "application.do-another-thing-timer", hasKeyWithAppName("p50"),
            "application.do-another-thing-timer", hasKeyWithAppName("p75"),
            "application.do-another-thing-timer", hasKeyWithAppName("p95"),
            "application.do-another-thing-timer", hasKeyWithAppName("p98"),
            "application.do-another-thing-timer", hasKeyWithAppName("p99"),
            "application.do-another-thing-timer", hasKeyWithAppName("p999"),
            "application.do-another-thing-timer", hasKeyWithAppName("oneMinRate"),
            "application.do-another-thing-timer", hasKeyWithAppName("fiveMinRate"),
            "application.do-another-thing-timer", hasKeyWithAppName("fifteenMinRate"),
            "application.do-another-thing-timer", hasKeyWithAppName("meanRate"),
            "application.do-another-thing-timer", hasKeyWithAppName("stddev")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Meter)`() {
        // it is a complex type of Counter
        verifyMetrics(
            "application.my-meter",  hasKeyWithAppName("count"),
            "application.my-meter",  hasKeyWithAppName("oneMinRate"),
            "application.my-meter",  hasKeyWithAppName("fiveMinRate"),
            "application.my-meter",  hasKeyWithAppName("fifteenMinRate"),
            "application.my-meter",  hasKeyWithAppName("meanRate")
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Gauge)`() {
        // A gauge is the simplest metric type that just returns a value
        verifyMetrics(
            withAppName("application.my-gauge"),  `is`(-99)
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (ConcurrentGauge)`() {
        // A gauge is the simplest metric type that just returns a value
        verifyMetrics(
            "application.my-concurrent-gauge",  hasKeyWithAppName("min"),
            "application.my-concurrent-gauge",  hasKeyWithAppName("max"),
            "application.my-concurrent-gauge",  hasKeyWithAppName("current")
        )
    }

    private fun verifyMetrics(path: String, matcher: Matcher<*>, vararg additionalKeyMatcherPairs: Any) {
        await().untilAsserted {
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

    private fun hasKeyWithAppName(key: String): Matcher<MutableMap<out String, *>> = hasKey(withAppName(key))

    private fun withAppName(key: String): String = "$key;_app=${contextPath()}"
}