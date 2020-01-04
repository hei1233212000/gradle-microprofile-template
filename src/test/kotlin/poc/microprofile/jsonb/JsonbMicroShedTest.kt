package poc.microprofile.jsonb

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test
import org.microshed.testing.jaxrs.RESTClient
import org.microshed.testing.jupiter.MicroShedTest
import org.microshed.testing.testcontainers.ApplicationContainer
import org.testcontainers.junit.jupiter.Container

@MicroShedTest
class JsonbMicroShedTest {
    companion object {
        @JvmField
        @Container
        val applicationContainer: ApplicationContainer = ApplicationContainer()
            /*
             * there is a restriction in MicroShed that it can only locate the JAXRS Application class in the
             * same package of the resource class or under the 3rd package, e.g: com.foo.bar.*
             * so we have to append the application path "api"
             */
            .withAppContextRoot("/gradle-microprofile-template/api")
            .withReadinessPath("/health/ready")

        @JvmStatic
        @RESTClient
        lateinit var jsonbApi: JsonbResource
    }

    @Test
    fun `verify Json-b is supported in Microprofile`() {
        val response = jsonbApi.jsonbModel()
        response `should not be` null
        response.id `should be equal to` 1
        response.a `should be equal to` "any value"
    }
}