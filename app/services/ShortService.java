package services;

import models.Account;
import models.Rule;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by vnazarov on 20/02/17.
 */
@Singleton
public class ShortService {

  private Map<String, Account> accounts;
  private Map<String, Rule> rules;
  private Map<String,String> tokens;

  public ShortService(){
    accounts = new ConcurrentHashMap<>();
    rules = new ConcurrentHashMap<>();
    tokens = new ConcurrentHashMap<>();
    Init();
  }
  //add account for test reason by curl
  protected void Init(){
    addAccount(new Account("local","test"));
  }


  public Account accountById(String accountId){
    return accounts.getOrDefault(accountId, null);
  }

  public Account accountByToken(String token){
    return accountById(tokens.getOrDefault(token,""));
  }

  public Account addAccount(Account account){
    Account oldAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (oldAccount != null) {
      // account with that ID already exists
      return null;
    }
    tokens.put(account.getToken(),account.getAccountId());
    return account;
  }

  public Rule ruleById(String shortUri){
    return rules.getOrDefault(shortUri, null);
  }

  public Rule addRule(Rule rule){
    rule.setShortUrl(generateUnique(8));
    while (rules.putIfAbsent(rule.getShortUrl(), rule )!=null){
      rule.setShortUrl(generateUnique(8));
    }
    return rule;
  }

  public List<Rule> rulesByAccountId(String accountId){
    return rules.values().stream()
            .filter((r) -> (accountId.compareTo(r.getAccountId())==0))
            .collect(Collectors.toList());
  }


  public static String generateUnique(int size){
    final String alphabet ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    int n = alphabet.length();
    StringBuilder sb = new StringBuilder(size);
    Random r = new Random();
    for (int i=0; i<size; i++) {
      sb.append(alphabet.charAt(r.nextInt(n)));
    }
    return sb.toString();
  }

}
