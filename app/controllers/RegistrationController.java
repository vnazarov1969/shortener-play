package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.Rule;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.ShortService;
import views.AccountView;
import views.RegisterView;

import javax.inject.Inject;

public class RegistrationController extends Controller {

  protected ShortService shortService;
  @Inject
  public RegistrationController(ShortService service){
    shortService = service;
  }

  protected Account currentAccount(){
    return (Account) Controller.ctx().args.get("account");
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
    if (account == null){
      return status(CONFLICT,new AccountView(account).render());
    }
    return created(new AccountView(account).render());
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
    String absoluteShortUrl = routes.RedirectController.goRedirect(rule.getShortUrl()).absoluteURL(request());

    return created(new RegisterView(absoluteShortUrl).render());
  }




}
