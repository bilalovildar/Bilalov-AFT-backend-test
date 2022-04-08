package Less4;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lesson4.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class BackApi4Test {
    private final String apiKey = "5b8b1b3075534a30b782cd489219e17a";
    ResponseSpecification responseSpecification = null;
    RequestSpecification requestSpecification = null;

    @BeforeEach
    void beforeTest() {
        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey)
                .log(LogDetail.ALL)
                .build();

        responseSpecification = new ResponseSpecBuilder()
//                .expectStatusCode(200)
//                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();
    }

    @Test
    void getNoAuthNegativeTest() {
        given()
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch?" +
                        "apiKey=45646545")
                .then()
                .spec(responseSpecification)
                .statusCode(401);
    }

    @Test
    void getAuthPozitiveTest() {
        given().spec(requestSpecification)
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .then()
                .spec(responseSpecification)
                .statusCode(200);
    }

    @Test
    void getRecipeCheckBurgerTitleTest() {
        Response response = given().spec(requestSpecification)
                .queryParam("query", "burger")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch").prettyPeek()
                .then()
                .extract()
                .body()
                .as(Response.class);
        assertThat(response.results[0].getTitle(), containsString("Falafel Burger"));
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

    @Test
    void addmealplannerTest() {
        String id = given()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", apiKey)
                .body("{\n" +
                        "    \"date\": 1589500800,\n" +
                        "    \"slot\": 1,\n" +
                        "    \"position\": 0,\n" +
                        "    \"type\": \"INGREDIENTS\",\n" +
                        "    \"value\": {\n" +
                        "        \"ingredients\": [\n" +
                        "            {\n" +
                        "                \"name\": \"1 banana\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"name\": \"coffee\",\n" +
                        "                \"unit\": \"cup\",\n" +
                        "                \"amount\": \"1\",\n" +
                        "                \"image\": \"https://spoonacular.com/cdn/ingredients_100x100/brewed-coffee.jpg\"\n" +
                        "            },\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/geekbrains/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        given()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", apiKey)
                .delete("https://api.spoonacular.com/mealplanner/geekbrains/items/" + id)
                .then()
                .statusCode(200);
    }
}


