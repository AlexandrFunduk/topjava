package ru.javawebinar.topjava.util;

import org.springframework.lang.Nullable;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {
    private Util() {
    }

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0) && (end == null || value.compareTo(end) < 0);
    }

    public static void addRole(ResultSet rs, User user) throws SQLException {
        String role = rs.getString("role");
        if (role != null) {
            user.getRoles().add(Role.valueOf(role));
        }
    }

}