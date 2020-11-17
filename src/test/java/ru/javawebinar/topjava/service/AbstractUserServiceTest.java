package ru.javawebinar.topjava.service;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractUserServiceTest extends AbstractServiceTest {

    @Autowired
    protected UserService service;

    @Test
    public void testA_update() {
        User updated = getUpdated();
        service.update(updated);
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
        Assertions.assertThat(service.getAll().contains(getUpdated())).isTrue();
    }

    @Test
    public void testB_getAll() {
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, user);
    }

    @Test
    public void testC_create() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void testD_duplicateMailCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new User(null, "Duplicate", "user@yandex.ru", "newPass", Role.USER, Role.ADMIN)));
    }

    @Test
    public void testE_get() {
        User user = service.get(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    public void testF_getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    public void testG_getByEmail() {
        User user = service.getByEmail("admin@gmail.com");
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    public void testH_delete() {
        service.delete(ADMIN_ID);
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_ID));
    }

    @Test
    public void testI_deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    public void testJ_createWithException() {
        validateRootCause(() -> service.create(new User(null, "  ", "mail@yandex.ru", "password", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "  ", "password", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "  ", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "password", 9, true, new Date(), Set.of())), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "password", 10001, true, new Date(), Set.of())), ConstraintViolationException.class);
    }

    @Test
    public void testK_setNullRoles() {
        User user = new User(admin);
        user.setRoles(null);
        service.update(user);
        user = new User(admin);
        user.setRoles(null);
        USER_MATCHER.assertMatch(service.get(ADMIN_ID), user);
    }

    @Test
    public void testL_setEmptyRoles() {
        User user = new User(admin);
        user.setRoles(Collections.emptySet());
        service.update(user);
        user = new User(admin);
        user.setRoles(Collections.emptySet());
        USER_MATCHER.assertMatch(service.get(ADMIN_ID), user);
    }

    @Test
    public void testM_createUserWithEmptyRole() {
        User user = getNew();
        user.setRoles(Collections.emptySet());
        User created = service.create(user);
        user = getNew();
        user.setId(created.getId());
        user.setRoles(Collections.emptySet());
        USER_MATCHER.assertMatch(created, user);
    }

    @Test
    public void testN_createUserWithSomeRole() {
        User user = getNew();
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        User created = service.create(user);
        user = getNew();
        user.setId(created.getId());
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        USER_MATCHER.assertMatch(created, user);
    }
}