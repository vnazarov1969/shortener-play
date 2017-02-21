package models;


import java.util.Base64;

/**
 * Created by vnazarov on 20/02/17.
 */

public class Account  {
  private String accountId;
  private String password;

  public Account(){};

  public Account(String accountId, String password){
    this.accountId = accountId;
    this.password = password;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken(){ return Base64.getEncoder().encodeToString(String.format("%s:%s",accountId,password).getBytes());}
}
