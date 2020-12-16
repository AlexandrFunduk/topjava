package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;
import static ru.javawebinar.topjava.util.exception.ErrorType.VALIDATION_ERROR;

class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Autowired
    private UserService userService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + 1)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getByEmail() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + admin.getEmail())
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> userService.get(USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + 1)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        User updated = UserTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        User newUser = UserTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(newUser, "newPass")))
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin, user));
    }

    @Test
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(userService.get(USER_ID).isEnabled());
    }

    @Test
    void getWithMeals() throws Exception {
        assumeDataJpa();
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID + "/with-meals")
                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_WITH_MEALS_MATCHER.contentJson(admin));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateEmail() throws Exception {
        User duplicateUser = new User(null, "duplicate", user.getEmail(), "123456", 2000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(duplicateUser, duplicateUser.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(type(DATA_ERROR))
                .andExpect(message("user.duplicateEmail"));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateEmail() throws Exception {
        User duplicateUser = new User(admin);
        duplicateUser.setEmail(user.getEmail());
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL + "/" + admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(duplicateUser, duplicateUser.getPassword())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(type(DATA_ERROR))
                .andExpect(message("user.duplicateEmail"));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createNotValidName() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "123456", 2000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createNotValidPassword() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "1", 2000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createNotValidCaloriesPerDay() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "123456", 20000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateNotValidName() throws Exception {
        User notValidUser = new User(admin);
        notValidUser.setName("");
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL + "/" + admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateNotValidPassword() throws Exception {
        User notValidUser = new User(admin);
        notValidUser.setPassword("2");
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL + "/" + admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateNotValidCaloriesPerDay() throws Exception {
        User notValidUser = new User(admin);
        notValidUser.setCaloriesPerDay(1);
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL + "/" + admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(notValidUser, notValidUser.getPassword())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }
}