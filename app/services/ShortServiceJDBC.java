package services;

import models.Account;
import models.Rule;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import play.Logger;
import play.db.*;



/**
 * Created by vnazarov on 27/02/17.
 */
@Singleton
public class ShortServiceJDBC implements IShortService  {

    private Database db;

    @Inject
    public void ShortServiceJDBC(Database db){
        this.db = db;
 //       Init();
    }

    //add account for test reason by curl
    protected void Init(){
        addAccount(new Account("local","test"));
    }


    @Override
    public Account accountById(String accountId) {
        final String sql = "SELECT ID, PASSWORD FROM ACCOUNT WHERE ID = ?";
        Account result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Account(rs.getString("ID"), rs.getString("PASSWORD"));
            }
        }catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Account accountByToken(String token) {
        final String sql = "SELECT ID, PASSWORD FROM ACCOUNT WHERE TOKEN = ?";
        Account result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Account(rs.getString("ID"), rs.getString("PASSWORD"));
            }
        }catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Account addAccount(Account account) {
        final String sql = "INSERT INTO ACCOUNT(ID, TOKEN, PASSWORD) VALUES (?,?,?)";
        Account result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,account.getAccountId());
            ps.setString(2,account.getToken());
            ps.setString(3,account.getPassword());
            if (ps.executeUpdate() > 0) {
                result = account;
            }
        }
        catch(SQLIntegrityConstraintViolationException e){
            Logger.debug(e.getMessage());
        }
        catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Rule ruleById(String shortUri) {
        final String sql = "SELECT SHORT_URL, ACCOUNT_ID, LONG_URL, REDIRECT_TYPE, REDIRECT_COUNT FROM RULE WHERE SHORT_URL = ?";
        Rule result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, shortUri);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Rule(
                        rs.getString("SHORT_URL"),
                        rs.getString("ACCOUNT_ID"),
                        rs.getString("LONG_URL"),
                        rs.getInt("REDIRECT_TYPE"),
                        rs.getLong("REDIRECT_COUNT"));
            }
        }catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Rule addRule(Rule rule, Account account) {
        final String sql = "INSERT INTO RULE(SHORT_URL, ACCOUNT_ID, LONG_URL, REDIRECT_TYPE, REDIRECT_COUNT) VALUES (?,?,?,?,?)";
        Rule result = null;
        rule.setShortUrl(Helper.generateUniqueString(8));
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            while (result == null) {
                ps.setString(1, rule.getShortUrl());
                ps.setString(2, account.getAccountId());
                ps.setString(3, rule.getLongUrl());
                ps.setInt(4, rule.getRedirectType());
                ps.setLong(5, rule.getCount());
                try {
                    ps.execute();
                    result = rule;
                } catch (SQLIntegrityConstraintViolationException e) {
                    rule.setShortUrl(Helper.generateUniqueString(8));
                }
            }
        }catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public List<Rule> rulesByAccountId(String accountId) {
        final String sql = "SELECT SHORT_URL, ACCOUNT_ID, LONG_URL, REDIRECT_TYPE, REDIRECT_COUNT FROM RULE WHERE ACCOUNT_ID = ?";

        List<Rule> result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            result = new LinkedList<>();
            while (rs.next()) {
                result.add(new Rule(
                        rs.getString("SHORT_URL"),
                        rs.getString("ACCOUNT_ID"),
                        rs.getString("LONG_URL"),
                        rs.getInt("REDIRECT_TYPE"),
                        rs.getLong("REDIRECT_COUNT")));
            }
        }
        catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Long incrementRedirectCount(String shortUrl){
        final String sql = "SELECT REDIRECT_COUNT FROM RULE WHERE SHORT_URL = ?";
        final String sqlUpdate = "UPDATE RULE SET REDIRECT_COUNT = ? WHERE SHORT_URL = ? AND REDIRECT_COUNT = ?";
        Long result = null;
        try(
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate))
        {
            while (result == null) {
                ps.setString(1, shortUrl);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new SQLException(String.format(" No data for rule %s", shortUrl));
                }
                Long count = rs.getLong("REDIRECT_COUNT");
                psUpdate.setLong(1, count + 1);
                psUpdate.setString(2, shortUrl);
                psUpdate.setLong(3, count);
                if (psUpdate.executeUpdate() > 0){
                    result = count + 1;
                }
            }
        }catch(SQLException e){
            Logger.error(e.getMessage());
        }
        return result;
    }

}
