package Less3;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BackApiTest {
    private final String apiKey = "5b8b1b3075534a30b782cd489219e17a";

    @Test
    void getNoAuthNegativeTest() {
        given()
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch?" +
                        "apiKey=45646545")
                .then()
                .statusCode(401);
    }

    @Test
    void getAuthPozitiveTest() {
        given()
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch?" +
                        "apiKey=" + apiKey)
                .then()
                .statusCode(200);
    }

    @Test
    void getRecipeCheckBurgerTitleTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "burger")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .body()
                .jsonPath()
                .prettyPeek();
        assertThat(response.get("results.title[0]"), equalTo("Falafel Burger"));
    }

    @Test
    void getRecipeCheckOffsetPositiveTest() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("offset", "394")
                .expect()
                .body("offset", equalTo(394))
//                .wait(200)
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch");
    }

    @Test
    void getRecipeSearchNegativeTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "<insert>!/**&^%$>")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .body()
                .jsonPath()
                .prettyPeek();
        assertThat(response.get("offset"), equalTo(0));
        assertThat(response.get("number"), equalTo(10));
        assertThat(response.get("totalResults"), equalTo(0));

    }

}
