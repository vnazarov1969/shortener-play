package services;

import com.google.inject.ImplementedBy;
import models.Account;
import models.Rule;

import java.util.List;

/**
 * Created by vnazarov on 27/02/17.
 */

@ImplementedBy(ShortService.class)
public interface IShortService {
    public Account accountById(String accountId);
    public Account accountByToken(String token);
    public Account addAccount(Account account);
    public Rule ruleById(String shortUri);
    public Rule addRule(Rule rule, Account account);
    public List<Rule> rulesByAccountId(String accountId);
    public Long incrementRedirectCount(String accountId);
}
