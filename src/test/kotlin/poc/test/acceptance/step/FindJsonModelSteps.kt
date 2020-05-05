package poc.test.acceptance.step

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import poc.microprofile.Application.Companion.API_PATH
import poc.test.acceptance.StartupServerInCucumber.Companion.serverPort

class FindJsonModelSteps : En {
    init {
        When("I query JsonModel and I should see:") { dataTable: DataTable ->
            val expectedJsonModel: Map<String, String> = dataTable.asMap(String::class.java, String::class.java)
            RestAssured.given()
                .accept(ContentType.JSON)
            .`when`()
                .get("http://localhost:${serverPort()}/$API_PATH/jsonb")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Is.`is`(expectedJsonModel["id"]?.toInt()))
                .body("custom-field", IsEqual.equalTo(expectedJsonModel["custom-field"]))
        }
    }
}