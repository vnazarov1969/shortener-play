import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.Base64;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class FunctionalTest extends WithApplication {
    @Override
    protected play.Application provideApplication() {
      return fakeApplication(inMemoryDatabase("test"));
    }

  @Test
  public void help() {
    RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/help");
    Result result = route(request);
    assertEquals(OK, result.status());
    assertEquals("text/html", result.contentType().get());
  }

  @Test
  public void root() {
    RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/");
    Result result = route(request);
    assertEquals(OK, result.status());
    assertEquals("text/html", result.contentType().get());
  }
  @Test
  public void account() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/account")
            .bodyJson(Json.parse("{\"AccountId\":\"test\"}"));
    Result result = route(request);
    assertEquals(CREATED, result.status());
    assertEquals("application/json", result.contentType().get());

    JsonNode jsn = Json.parse(contentAsString(result));
    assertEquals(jsn.findPath("success").asText(),"true");
    assertEquals(jsn.findPath("description").asText(),"Your account is opened");
    assertEquals(jsn.findPath("password").asText().length(),8);
  }

  @Test
  public void accountExists() {
    RequestBuilder request0 = new RequestBuilder()
            .method(POST)
            .uri("/account")
            .bodyJson(Json.parse("{\"AccountId\":\"test\"}"));
    Result result0 = route(request0);
    assertEquals(CREATED, result0.status());

    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/account")
            .bodyJson(Json.parse("{\"AccountId\":\"test\"}"));
    Result result = route(request);
    assertEquals(CONFLICT, result.status());
    assertEquals("application/json", result.contentType().get());

    JsonNode jsn = Json.parse(contentAsString(result));
    assertEquals("false", jsn.findPath("success").asText());
    assertEquals("account with that ID already exists",jsn.findPath("description").asText());
    assertTrue(jsn.findPath("password").isMissingNode());
  }

  @Test
  public void accountMandatoryFields() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/account")
            .bodyJson(Json.parse("{\"AccountI\":\"test\"}"));
    Result result = route(request);
    assertEquals(BAD_REQUEST, result.status());
  }

  private static Result registerCall(String url, Integer redirectType){
    final String jsonPattern = redirectType == null?"{\"url\":\"%s\"}":"{\"url\":\"%s\",\"redirectType\":%d}";
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse(String.format(jsonPattern, url, redirectType)))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    return route(request);
  }

  static String encodeAuthHeader(String user, String password){
    return String.format("Basic %s", Base64.getEncoder().encodeToString(String.format("%s:%s",user,password).getBytes()));
  }

  @Test
  public void register() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"url\":\"http://ya.ru\"}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    Result result = route(request);
    assertEquals(CREATED, result.status());
    assertEquals("application/json", result.contentType().get());
    JsonNode jsn = Json.parse(contentAsString(result));
    assertTrue(jsn.findPath("shortUrl").asText().contains("http://localhost/"));
  }

  @Test
  public void register301() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"url\":\"http://ya.ru\",\"redirectType\":301}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    Result result = route(request);
    assertEquals(CREATED, result.status());
    assertEquals("application/json", result.contentType().get());
    JsonNode jsn = Json.parse(contentAsString(result));
    assertTrue(jsn.findPath("shortUrl").asText().contains("http://localhost/"));
  }
  @Test
  public void register302() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"url\":\"http://ya.ru\",\"redirectType\":302}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    Result result = route(request);
    assertEquals(CREATED, result.status());
    assertEquals("application/json", result.contentType().get());
    JsonNode jsn = Json.parse(contentAsString(result));
    assertTrue(jsn.findPath("shortUrl").asText().contains("http://localhost/"));
  }

  @Test
  public void registerMandatory() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"urli\":\"http://ya.ru\"}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    Result result = route(request);
    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void registerExists() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"url\":\"http://ya.ru\"}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","test"));
    Result result = route(request);
    assertEquals(CREATED, result.status());
    assertEquals("application/json", result.contentType().get());
    JsonNode jsn = Json.parse(contentAsString(result));
    assertTrue(jsn.findPath("shortUrl").asText().contains("http://localhost/"));
  }

  @Test
  public void registerAuth() {
    RequestBuilder request = new RequestBuilder()
            .method(POST)
            .uri("/register")
            .bodyJson(Json.parse("{\"uri\":\"http://ya.ru\",\"redirectType\":301}"))
            .header(AUTHORIZATION, encodeAuthHeader("local","testhhfkhwkhrkhfh"));
    Result result = route(request);
    assertEquals(UNAUTHORIZED, result.status());
  }



  @Test
  public void statistic() {
    doRedirect("http://yandex.ru",301,10);
    doRedirect("http://google.ru",302,20);
    RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri("/statistic/local");
    Result result = route(request);
    assertEquals(OK, result.status());
    assertEquals("application/json", result.contentType().get());
    JsonNode jsn = Json.parse(contentAsString(result));
    assertEquals(jsn.findPath("http://yandex.ru").asInt(),10);
    assertEquals(jsn.findPath("http://google.ru").asInt(), 20);
  }

  static void doRedirect(String url, Integer requestType, Integer count  ){
    JsonNode jsn = Json.parse(contentAsString(registerCall(url, requestType)));
    for (int i=0;i<count;i++) {
      RequestBuilder request = new RequestBuilder()
              .method(GET)
              .uri(jsn.findPath("shortUrl").asText());
      Result result = route(request);
      assertEquals(requestType.longValue(), result.status());
    }
  }

  @Test
  public void goRedirect() {
    JsonNode jsn = Json.parse(contentAsString(registerCall("http://yandex.ru",null)));
    RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri(jsn.findPath("shortUrl").asText());
    Result result = route(request);
    assertEquals(302, result.status());
    assertTrue(result.header("Location").orElse("").contains("yandex"));
  }

  @Test
  public void goRedirect301() {
    JsonNode jsn = Json.parse(contentAsString(registerCall("http://yandex.ru",301)));
    RequestBuilder request = new RequestBuilder()
            .method(GET)
            .uri(jsn.findPath("shortUrl").asText());
    Result result = route(request);
    assertEquals(301, result.status());
    assertTrue(result.header("Location").orElse("").contains("yandex"));
  }

}