package poc.test.acceptance

import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import org.awaitility.Awaitility.await
import org.microshed.testing.testcontainers.ApplicationContainer
import org.slf4j.LoggerFactory
import poc.microprofile.test.util.verifyIfServerIsReady

class StartupServerInCucumber : En {
    companion object {
        private val applicationContainer: ApplicationContainer = ApplicationContainer()

        fun serverPort(): Int = applicationContainer.firstMappedPort

        private val logger = LoggerFactory.getLogger(StartupServerInCucumber::class.java)
    }

    init {
        Before { _: Scenario ->
            startServer()
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

        val url = "http://localhost:${serverPort()}"
        verifyIfServerIsReady(url)
        logger.info("container is ready")
    }
}