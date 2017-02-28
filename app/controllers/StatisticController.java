package controllers;

import models.Rule;
import play.mvc.Controller;
import play.mvc.Result;
import services.IShortService;
import views.StatisticView;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vnazarov on 22/02/17.
 */
public class StatisticController extends Controller {
  protected IShortService shortService;
  @Inject
  public StatisticController(IShortService service){
    shortService = service;
  }


  public Result statistic(String accountId) {

    if (shortService.accountById(accountId)==null){
      return badRequest("Invalid account Id");
    }

    List<Rule> list = shortService.rulesByAccountId(accountId);

    // Aggregate by LongUrl
    Map<String,Long> map = new HashMap<>();
    for(Rule rule:list){
      Long val = map.get(rule.getLongUrl());
      map.put(rule.getLongUrl(), rule.getCount() + ((val == null)? 0 : val));
    }

    return ok(new StatisticView(map).render());
  }

}
