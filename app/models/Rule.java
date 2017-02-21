package models;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vnazarov on 20/02/17.
 */
public class Rule {
  private String accountId;
  private String shortUrl;
  private String longUrl;
  private Integer redirectType;
  private AtomicLong count;

  public Rule(String accountId, String longUrl, Integer redirectType){
    this.accountId = accountId;
    this.longUrl = longUrl;
    this.redirectType = redirectType;
    this.count = new AtomicLong(0);
  }

  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public String getLongUrl() {
    return longUrl;
  }

  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }

  public Integer getRedirectType() {
    return redirectType;
  }

  public void setRedirectType(Integer redirectType) {
    this.redirectType = redirectType;
  }

  public long getCount() {
    return count.get();
  }

  public String getAccountId() {
    return accountId;
  }

  public long incrementCount() {
    return count.incrementAndGet();
  }

}
