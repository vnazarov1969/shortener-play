package views;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by vnazarov on 21/02/17.
 */
public interface IView {
  public JsonNode render();
}
