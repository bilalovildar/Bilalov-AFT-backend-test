package Less3;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import io.restassured.RestAssured;
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

    @Test
    public void postRecipesCuisine1Test() {
        given()
                .queryParam("apiKey", apiKey)
                .when().post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .assertThat().statusCode(200)
                .and()
                .body("cuisine", is("Mediterranean"));
    }

    @Test
    public void postRecipesCuisineBodyTest() {
        given()
                .queryParam("apiKey", apiKey)
                .body("{\n" +
                        "    \"title\": \"Chicken Spinach Mozzarella\",\n" +
                        "    \"ingredientList\": \"4 oz pork shoulder\",\n" +
                        "    \"language\": \"en\"\n" +
                        "}")
                .when().post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .assertThat().statusCode(200)
                .and()
                .body("confidence", is(0.0F));
    }

    @Test
    public void postRecipesCuisineEmptyBodyTest() {
        given()
                .queryParam("apiKey", apiKey)
                .body(" ")
                .when().post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .assertThat().statusCode(200)
                .and()
                .body("confidence", is(0.0F));
    }

    @Test
    public void postRecipesCuisineWrongBody4Test() {
        given()
                .queryParam("apiKey", apiKey)
                .body("ubeebebeceuippp")
                .when().post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void postRecipesCuisine5Test() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("title", "Pie")
                .queryParam("ingredientList", "10 oz bread and 40 oz apple")
                .body("{\n" +
                        "    \"language\": \"en\"\n" +
                        "}")
                .when().post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .assertThat().statusCode(200)
                .and()
                .body("confidence", is(0.0F));
    }
}


