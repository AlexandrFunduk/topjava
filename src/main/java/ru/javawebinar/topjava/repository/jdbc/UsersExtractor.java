package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.javawebinar.topjava.util.Util.addRole;

@Component
public class UsersExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> users = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            User user = users.get(id);
            if (user == null) {
                user = new User();
                user.setId(id);
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setRegistered(rs.getDate("registered"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                user.setRoles(new HashSet<>());
                addRole(rs, user);
                users.put(id,user);
            }
            addRole(rs, user);
        }
        return new ArrayList<>(users.values());
    }
}
