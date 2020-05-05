package poc.test.acceptance

import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.hamcrest.core.Is
import org.microshed.testing.testcontainers.ApplicationContainer
import org.slf4j.LoggerFactory

class StartupServerInCucumber : En {
    companion object {
        private val applicationContainer: ApplicationContainer = ApplicationContainer()

        fun serverPort(): Int = applicationContainer.firstMappedPort

        private val logger = LoggerFactory.getLogger(StartupServerInCucumber::class.java)
    }

    init {
        Before { _: Scenario ->
            startServer()
            verifyIfServerIsReady()
        }

        After { _: Scenario ->
            applicationContainer.stop()
        }
    }

    private fun startServer() {
        applicationContainer.start()
        await().until {
            applicationContainer.isCreated
        }
        logger.info("container is started")
    }

    private fun verifyIfServerIsReady() {
        await().untilAsserted {
            RestAssured.given()
                .accept(ContentType.JSON)
            .`when`()
                .get("http://localhost:${serverPort()}/health/ready")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", Is.`is`("UP"))
        }
        logger.info("container is ready")
    }
}