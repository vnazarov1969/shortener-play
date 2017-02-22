import play.*;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.*;
import play.mvc.*;

import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by vnazarov on 22/02/17.
 */


@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

  @Inject
  public ErrorHandler(Configuration configuration, Environment environment,
                      OptionalSourceMapper sourceMapper, Provider<Router> routes) {
    super(configuration, environment, sourceMapper, routes);
  }

  protected CompletionStage<Result> onProdServerError(RequestHeader request, UsefulException exception) {
    return CompletableFuture.completedFuture(
            Results.internalServerError("A server error occurred: " + exception.getMessage())
    );
  }

  protected CompletionStage<Result> onForbidden(RequestHeader request, String message) {
    return CompletableFuture.completedFuture(
            Results.forbidden("You're not allowed to access this resource.")
    );
  }
}

