package poc.microprofile.test.util

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.hamcrest.core.Is

fun verifyIfServerIsReady(urlWithoutContext: String) {
    Awaitility.await().untilAsserted {
        RestAssured.given()
            .accept(ContentType.JSON)
        .`when`()
            .get("$urlWithoutContext/health/ready")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", Is.`is`("UP"))
    }
}