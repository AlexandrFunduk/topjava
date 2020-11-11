package ru.javawebinar.topjava.repository.jdbc;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import static ru.javawebinar.topjava.util.Util.addRole;

@Component
public class UserExtractor implements ResultSetExtractor<User>{

    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {

        User user = null;
        while (rs.next()) {
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setRegistered(rs.getDate("registered"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                user.setRoles(new HashSet<>());
                addRole(rs, user);
            }
            addRole(rs, user);
        }
        return user;
    }


}

