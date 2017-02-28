package controllers;

import models.Rule;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.IShortService;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Created by vnazarov on 22/02/17.
 */
public class RedirectController extends Controller {
  protected IShortService shortService;
  @Inject
  public RedirectController(IShortService service){
    shortService = service;
  }

  public Result goRedirect(String shortUrl) {
    Rule rule = shortService.ruleById(shortUrl);
    if (rule == null){
      return notFound();
    }
    shortService.incrementRedirectCount(shortUrl);
    return new Result(rule.getRedirectType(), Collections.singletonMap("Location", rule.getLongUrl()));
  }

}
