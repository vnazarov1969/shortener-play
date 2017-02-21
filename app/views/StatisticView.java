package views;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vnazarov on 21/02/17.
 */
public class StatisticView implements IView {
  private Map<String,Long> map;

  public StatisticView(Map<String,Long> map){
    this.map = map;
  };
  @Override
  public JsonNode render() {
    TreeMap sortedMap;
    sortedMap = new TreeMap(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    sortedMap.putAll(map);
    return Json.toJson(sortedMap);
  }
}
