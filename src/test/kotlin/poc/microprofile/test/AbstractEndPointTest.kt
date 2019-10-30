package poc.microprofile.test

import io.restassured.RestAssured
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.test.api.ArquillianResource
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle
import org.junit.Before
import java.io.File
import java.net.URI

internal abstract class AbstractEndPointTest {
    companion object {
        @JvmStatic
        private val thirdPartyLibraries = Gradle.resolver()
            .forProjectDirectory(".")
            .importCompileAndRuntime()
            .resolve()
            .asList(JavaArchive::class.java)!!

        @JvmStatic
        @Deployment
        fun createDeployment(): WebArchive {
            return ShrinkWrap.create(WebArchive::class.java)
                .addPackages(true, "poc")
                .addAsWebInfResource(File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
                .addAsResource("log4j2.xml", "log4j2.xml")
                .addAsResource("META-INF/microprofile-config.properties", "META-INF/microprofile-config.properties")
                .addAsResource("META-INF/openapi.yaml", "META-INF/openapi.yaml")
                .addAsResource("payara-mp-jwt.properties", "payara-mp-jwt.properties")
                .addAsResource("publicKey.pem", "publicKey.pem")
                .addAsLibraries(thirdPartyLibraries)
        }
    }

    @ArquillianResource
    lateinit var uri: URI

    @Before
    fun enableRestAssuredLogging() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        RestAssured.baseURI = uri.toString()
    }
}