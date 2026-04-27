package com.bank.bank_api.repository;

import com.bank.bank_api.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum)-> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    };

    public User getBalance(Long userId) {                   //Получаем баланс по id пользователя
        String sql = "select id, balance from users where id = ?";
        try{
           return jdbcTemplate.queryForObject(sql, userRowMapper,userId);
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean putMoney(Long userId, BigDecimal amount) {                      //снятие денег со счета
        String  sql = "update users set balance = balance + ? where id = ?";       //  если rowAffect >0 то операция прошла
        int rowAffect = jdbcTemplate.update(sql,amount, userId);
        return rowAffect>0;
    }

    public int takeMoney(Long userId, BigDecimal amount) {
        User user = getBalance(userId);
        if(user==null) {
            return -1;
        }
        if(user.getBalance().compareTo(amount)<0){
            return 0;
        }
        String sql = "update users set balance = balance-? where id= ? and balance>=?";
        int rowAffect = jdbcTemplate.update(sql, amount, userId, amount);
        return rowAffect>0? 1:0;
    }


}
