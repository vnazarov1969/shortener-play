package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.Rule;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.AuthAction;
import services.ShortService;
import views.AccountView;
import views.RegisterView;
import views.StatisticView;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

  protected ShortService shortService;
  @Inject
  public Application(ShortService service){
    shortService = service;
  }

  protected Account currentAccount(){
    return (Account) Controller.ctx().args.get("account");
  }


  public Result goRedirect(String shortUrl) {
    Rule rule = shortService.ruleById(shortUrl);
    if (rule == null){
      return status(NOT_FOUND);
    }
    rule.incrementCount();
    return new Result(rule.getRedirectType(), Collections.singletonMap("Location", rule.getLongUrl()));
  }


  public Result account() {
    JsonNode jsn = request().body().asJson();
    if (jsn == null){
      return badRequest("Invalid Json Format");
    }
    String accountId = jsn.findPath("AccountId").asText();
    if (accountId.length() < 1) {
      return badRequest("Invalid Json Format, 'AccointId' is required");
    }
    Account account = shortService.addAccount(new Account(accountId, shortService.generateUnique(8)));
    return ok(new AccountView(account).render());
  }

  @With(AuthAction.class)
  public Result register() {
    JsonNode jsn = request().body().asJson();
    if (jsn == null){
      return badRequest("Invalid Json Format");
    }
    String longUrl = jsn.findPath("url").asText();
    if (longUrl.length() < 1) {
      return badRequest("Invalid Json Format, 'url' is required");
    }

    Rule rule = new Rule(
            currentAccount().getAccountId(),
            jsn.findPath("url").asText(),
            jsn.findPath("redirectType").asInt(302));

    rule = shortService.addRule(rule);
    String absoluteShortUrl = routes.Application.goRedirect(rule.getShortUrl()).absoluteURL(request());

    return ok(new RegisterView(absoluteShortUrl).render());
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
