package controllers;

import models.Rule;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.IShortService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

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
    // Async counter incrementation of redirect
    CompletableFuture.supplyAsync(() -> shortService.incrementRedirectCount(shortUrl)).exceptionally(e ->{
      Logger.error(e.getMessage()); return new Long(0);} );

    return new Result(rule.getRedirectType(), Collections.singletonMap("Location", rule.getLongUrl()));
  }

}
