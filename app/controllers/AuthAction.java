package controllers;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.Logger;
import services.ShortService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by vnazarov on 20/02/17.
 */
public class AuthAction extends Action.Simple {
  private ShortService service;
  private static final String AUTHORIZATION = "authorization";
  @Inject
  public AuthAction(ShortService service){
    this.service = service;
  }
  @Override
  public CompletionStage<Result> call(Http.Context context) {
    if (service == null){
      return CompletableFuture.completedFuture(internalServerError("Dependency Error"));
    }
    String authToken = extractAuthToken(context.request().getHeader(AUTHORIZATION));

    if (authToken != null){
      Object account = service.accountByToken(authToken);
      if (account != null) {
        context.args.put("account", account);
        return delegate.call(context);
      }
    }
    return CompletableFuture.completedFuture(unauthorized("401 Not Authorized"));
  }

  private static String extractAuthToken(String header){
    if (header != null){
      String[] words = header.split(" ");
      if ((words.length == 2)&&(words[0].compareToIgnoreCase("basic")==0)) {
        return words[1];
      }
    }
    return null;
  }
}
