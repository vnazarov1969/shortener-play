package views;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import play.libs.Json;

/**
 * Created by vnazarov on 21/02/17.
 */
public class AccountView implements IView {

  private class AccountResponseData {
    public Boolean success;
    public String description;
    // we don't send password if it is null or empty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String password;
  }


  protected Account account;
  public AccountView(Account account){
    this.account = account;
  }
  @Override
  public JsonNode render() {
    AccountResponseData result = new AccountResponseData();
    if (account == null){
      result.success = false;
      result.description = "account with that ID already exists";
    }else {
      result.success = true;
      result.description = "Your account is opened";
      result.password = account.getPassword();
    }
    return Json.toJson(result);
  }
}
