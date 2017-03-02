package services;

import models.Account;
import models.Rule;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vnazarov on 01/03/17.
 */

@Singleton
public class ShortServiceCached implements IShortService  {
    protected IShortService service;
    private Map<String, Rule> rules;
    private Map<String, Account> tokens;

    @Inject
    public ShortServiceCached(ShortServiceJDBC service){
        this.service = service;
        this.rules = new ConcurrentHashMap<>();
        this.tokens = new ConcurrentHashMap<>();
    }

    @Override
    public Account accountById(String accountId) {
        return service.accountById(accountId);
    }

    @Override
    public Account accountByToken(String token) {
        Account result = tokens.get(token);
        if (result == null) {
            result = service.accountByToken(token);
            if (result!=null) {
                tokens.putIfAbsent(token, result);
            }
        }
        return result;
    }

    @Override
    public Account addAccount(Account account) {
        return service.addAccount(account);
    }

    @Override
    public Rule ruleById(String shortUri) {
        Rule result = rules.get(shortUri);
        if (result == null) {
            result = service.ruleById(shortUri);
            if (result!=null) {
                rules.putIfAbsent(result.getShortUrl(), result);
            }
        }
        return result;
    }

    @Override
    public Rule addRule(Rule rule, Account account) {
        return service.addRule(rule, account);
    }

    @Override
    public List<Rule> rulesByAccountId(String accountId) {
        return service.rulesByAccountId(accountId);
    }

    @Override
    public Long incrementRedirectCount(String accountId) {
        return service.incrementRedirectCount(accountId);
    }
}
