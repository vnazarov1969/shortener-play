package views;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import models.Rule;
import play.libs.Json;

/**
 * Created by vnazarov on 21/02/17.
 */
public class RegisterView implements IView {

  private class RegisterResponseData {
    public String shortUrl;
  }
  private RegisterResponseData data;

  public RegisterView(String shortUrl){
      data = new RegisterResponseData();
      data.shortUrl = shortUrl;
  }

  @Override
  public JsonNode render() {
    return Json.toJson(data);
  }
}
