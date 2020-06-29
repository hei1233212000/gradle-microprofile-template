package poc.microprofile.test.util

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.hamcrest.core.Is

fun verifyIfServerIsReady(urlWithoutContext: String) {
    await().untilAsserted {
        given()
            .accept(ContentType.JSON)
        .`when`()
            .get("$urlWithoutContext/health/ready")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", Is.`is`("UP"))
    }
}