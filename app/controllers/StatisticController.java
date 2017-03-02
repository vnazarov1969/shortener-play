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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by vnazarov on 22/02/17.
 */
public class StatisticController extends Controller {
  protected IShortService shortService;
  @Inject
  public StatisticController(IShortService service){
    shortService = service;
  }


  public CompletionStage<Result> statistic(String accountId) {

    if (shortService.accountById(accountId)==null){
      return CompletableFuture.completedFuture(badRequest("Invalid account Id"));
    }
    // Use Async call for aggregating, to use not-bloking in main request rendering
    CompletionStage<Map<String,Long>> stageMap = CompletableFuture.supplyAsync(() -> {
      List<Rule> list = shortService.rulesByAccountId(accountId);
      // Aggregate by LongUrl
      Map<String,Long> map = new HashMap<>();
      for(Rule rule:list){
        Long val = map.get(rule.getLongUrl());
        map.put(rule.getLongUrl(), rule.getCount() + ((val == null)? 0 : val));
      }
      return map;
    });
    return stageMap.thenApply(map -> ok(new StatisticView(map).render()));
  }

}
