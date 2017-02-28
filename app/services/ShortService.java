package services;

import com.google.inject.ImplementedBy;
import models.Account;
import models.Rule;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * Created by vnazarov on 20/02/17.
 */
@Singleton
public class ShortService implements IShortService{

  Configuration configuration;
  Environment environment;

  private Map<String, Account> accounts;
  private Map<String, Rule> rules;
  private Map<String,String> tokens;
  private Map<String,Deque<Rule>> rulesByAccount;


  @Inject
  public ShortService(
          Environment environment,
          Configuration configuration,
          ApplicationLifecycle lifecycle
  ){
    this.configuration = configuration;
    this.environment = environment;

    lifecycle.addStopHook(() -> {
      StoreData();
      return CompletableFuture.completedFuture(null);
    });

    accounts = new ConcurrentHashMap<>();
    rules = new ConcurrentHashMap<>();
    tokens = new ConcurrentHashMap<>();
    rulesByAccount = new ConcurrentHashMap<>();
    LoadData();
//    Init();
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

  private Rule addRuleToAccount(Rule rule){
    Deque<Rule> deque = rulesByAccount.putIfAbsent(rule.getAccountId(), new ConcurrentLinkedDeque<>());
    if (deque == null) {
      deque = rulesByAccount.get(rule.getAccountId());
    }
    deque.add(rule);
    return rule;
  }

  public Rule addRule(Rule rule, Account account){
    rule.setShortUrl(Helper.generateUniqueString(8));
    while (rules.putIfAbsent(rule.getShortUrl(), rule )!=null){
      rule.setShortUrl(Helper.generateUniqueString(8));
    }
    addRuleToAccount(rule);
    return rule;
  }

  public List<Rule> rulesByAccountId(String accountId){
    Deque<Rule> deque = rulesByAccount.get(accountId);
    return (deque != null)?deque.stream().collect(Collectors.toList()):null;
  }

  private void StoreData() {
    String dbpath = configuration.getString("DBPATH");
    if (dbpath == null){
      Logger.info("Data File not configured, Application didn't save data");
      return;
    }
    File file = environment.getFile(dbpath);

    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      ObjectOutputStream s = new ObjectOutputStream(os);
      s.writeObject(accounts);
      s.writeObject(rules);
      s.close();
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
  }


  private void LoadData(){
    String dbpath = configuration.getString("DBPATH");
    if (dbpath == null){
      Logger.info("Data File not configured, Application starts with empty configuration");
      return;
    }
    File file = environment.getFile(dbpath);
    if (!file.exists()){
      Logger.info("Data File {} not found, Application starts with empty configuration",dbpath);
      return;
    }

    InputStream is = null;
    try{
      is = new FileInputStream(file);

      ObjectInputStream s = new ObjectInputStream(is);
      accounts = (ConcurrentHashMap<String,Account>)s.readObject();
      rules = (ConcurrentHashMap<String,Rule>)s.readObject();
      s.close();
    }
    catch (ClassNotFoundException e){
      Logger.error(e.getMessage());
    }
    catch (IOException e){
      Logger.error(e.getMessage());
    }
    ReCalculateReferences();
  }

  private void ReCalculateReferences(){
    accounts.values().forEach(a -> tokens.put(a.getToken(), a.getAccountId()));
    rules.values().forEach(r -> addRuleToAccount(r));
  }

  public Long incrementRedirectCount(String shortUrl){
    return rules.get(shortUrl).incrementCount();
  }


}
