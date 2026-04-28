package com.bank.bank_api.repository;

import com.bank.bank_api.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    };

    public User getBalance(Long userId) {                   //Получаем баланс по id пользователя
        String sql = "select id, balance from users where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public boolean putMoney(Long userId, BigDecimal amount) {                      //снятие денег со счета
        String updateSql = "update users set balance = balance + ? where id = ?";
        int rowAffect = jdbcTemplate.update(updateSql, amount, userId);

        if (rowAffect > 0) {
            String insertSql = "insert into transactions (user_id, operation_type, amount, operation_date)" +
                    " values (?, 'PUT', ?, ?)";
            jdbcTemplate.update(insertSql, userId, amount, Timestamp.valueOf(LocalDateTime.now()));
            return true;

        }
        return false;
    }

    @Transactional
    public int takeMoney(Long userId, BigDecimal amount) {
        User user = getBalance(userId);
        if (user == null) {
            return -1;
        }
        if (user.getBalance().compareTo(amount) < 0) {
            return 0;
        }
        String updateSql = "update users set balance = balance-? where id= ? and balance>=?";
        int rowAffect = jdbcTemplate.update(updateSql, amount, userId, amount);

        if (rowAffect > 0) {
            String insertSql = "insert into transactions (user_id, operation_type, amount, operation_date) " +
                    "Values (?, 'TAKE', ?, ?)";
            jdbcTemplate.update(insertSql, userId, amount, Timestamp.valueOf(LocalDateTime.now()));
            return 1;
        }
        return 0;
    }

    public List<Map<String, Object>> getOperationList(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sql = new StringBuilder("select id, user_id, operation_type, amount, operation_date " +
                "from transactions where user_id = ? ");
        List<Object> param = new ArrayList<>();
        param.add(userId);

        if (startDate != null) {
            sql.append("and operation_date >= ?");
            param.add(Timestamp.valueOf(startDate));
        }

        if(endDate!=null) {
            sql.append("and operation_date<= ?");
            param.add(Timestamp.valueOf(endDate));
        }

        sql.append("order by operation_date desc");
        return jdbcTemplate.queryForList(sql.toString(), param.toArray());
    }


}
