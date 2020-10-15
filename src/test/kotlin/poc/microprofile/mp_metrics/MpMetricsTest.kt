package poc.microprofile.mp_metrics

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import org.amshove.kluent.`should be equal to`
import org.awaitility.Awaitility.await
import org.hamcrest.Matcher
import org.hamcrest.collection.IsMapContaining.hasKey
import org.hamcrest.core.StringStartsWith.startsWith
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
        val response = verifyMetrics(
            "$", hasKey("application")
        )

        val applicationMetrics = response.path<Map<String, String>>("application")
        val doSomethingCountKey = applicationMetrics.keys.first { it.startsWith("do-something-count") && it.endsWith("tag1=value1")}
        applicationMetrics[doSomethingCountKey] `should be equal to` 2

        val doAnotherThingCountKey = applicationMetrics.keys.first { it.startsWith("do-another-thing-count") && it.endsWith("tag2=value2")}
        applicationMetrics[doAnotherThingCountKey] `should be equal to` 1
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Timer)`() {
        val response = verifyMetrics(
            "$", hasKey("application"),
            "application.do-another-thing-timer", hasKey(startsWith("max")),
            "application.do-another-thing-timer", hasKey(startsWith("min")),
            "application.do-another-thing-timer", hasKey(startsWith("mean")),
            "application.do-another-thing-timer", hasKey(startsWith("p50")),
            "application.do-another-thing-timer", hasKey(startsWith("p75")),
            "application.do-another-thing-timer", hasKey(startsWith("p95")),
            "application.do-another-thing-timer", hasKey(startsWith("p98")),
            "application.do-another-thing-timer", hasKey(startsWith("p99")),
            "application.do-another-thing-timer", hasKey(startsWith("p999")),
            "application.do-another-thing-timer", hasKey(startsWith("oneMinRate")),
            "application.do-another-thing-timer", hasKey(startsWith("fiveMinRate")),
            "application.do-another-thing-timer", hasKey(startsWith("fifteenMinRate")),
            "application.do-another-thing-timer", hasKey(startsWith("meanRate")),
            "application.do-another-thing-timer", hasKey(startsWith("stddev"))
        )

        val timerMetrics = response.path<Map<String, String>>("application.do-another-thing-timer")
        val countKey = timerMetrics.keys.first { it.startsWith("count") }
        timerMetrics[countKey] `should be equal to` 1
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Meter)`() {
        // it is a complex type of Counter
        verifyMetrics(
            "application.my-meter",  hasKey(startsWith("count")),
            "application.my-meter",  hasKey(startsWith("oneMinRate")),
            "application.my-meter",  hasKey(startsWith("fiveMinRate")),
            "application.my-meter",  hasKey(startsWith("fifteenMinRate")),
            "application.my-meter",  hasKey(startsWith("meanRate"))
        )
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (Gauge)`() {
        // A gauge is the simplest metric type that just returns a value
        val response = verifyMetrics(
            "$", hasKey("application")
        )
        val applicationMetrics = response.path<Map<String, String>>("application")
        val myGaugeKey = applicationMetrics.keys.first { it.startsWith("my-gauge") }
        applicationMetrics[myGaugeKey] `should be equal to` -99
    }

    @Test
    @RunAsClient
    fun `should able to get the metrics of the application (ConcurrentGauge)`() {
        // A gauge is the simplest metric type that just returns a value
        verifyMetrics(
            "application.my-concurrent-gauge",  hasKey(startsWith("min")),
            "application.my-concurrent-gauge",  hasKey(startsWith("max")),
            "application.my-concurrent-gauge",  hasKey(startsWith("current"))
        )
    }

    private fun verifyMetrics(path: String, matcher: Matcher<*>, vararg additionalKeyMatcherPairs: Any): Response {
        var body: ValidatableResponse? = null
        await().untilAsserted {
            body = given()
                    .accept(ContentType.JSON)
                .`when`()
                    .get("${baseUrlWithoutContext()}/metrics")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(path, matcher, *additionalKeyMatcherPairs)
        }
        return body!!.extract().response()
    }
}