package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Transactional(readOnly = true)
@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(@NotNull User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        ValidationUtil.validateBean(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        } else {
            deleteRoles(user.id());
        }
        Set<Role> roles = user.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            saveRoles(List.copyOf(roles), user.id());
        }
        return user;
    }

    private void deleteRoles(int id) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", id);
    }

    private void saveRoles(final List<Role> roles, int id) {
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (role, user_id) VALUES (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, roles.get(i).name());
                ps.setInt(2, id);
            }

            @Override
            public int getBatchSize() {
                return roles.size();
            }
        });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ur on users.id = ur.user_id WHERE id=?",
                new UsersExtractor(),
                id);

        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(@Email String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ur on users.id = ur.user_id WHERE email=?",
                new UsersExtractor(),
                email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ur on users.id = ur.user_id ORDER BY name, email",
                new UsersExtractor());
    }

    // https://stackoverflow.com/questions/16461735/jdbc-template-one-to-many
    private static class UsersExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> users = new LinkedHashMap<>();
            int rowNum = 0;
            UserRowMapper rowMapper = new UserRowMapper();
            while (rs.next()) {
                int id = rs.getInt("id");
                User user = users.get(id);
                if (user == null) {
                    user = rowMapper.mapRow(rs, rowNum);
                    users.put(id, user);
                }
                String role = rs.getString("role");
                if (role != null) {
                    Set<Role> roles = user.getRoles();
                    roles.add(Role.valueOf(role));
                    user.setRoles(roles);
                }
            }
            return new ArrayList<>(users.values());
        }
    }
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
           return new User(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("calories_per_day"),
                    rs.getBoolean("enabled"),
                    rs.getDate("registered"),
                    EnumSet.noneOf(Role.class));
        }
    }
}
