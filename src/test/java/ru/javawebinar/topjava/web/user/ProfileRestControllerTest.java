package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;
import static ru.javawebinar.topjava.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javawebinar.topjava.web.user.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(userService.getAll(), admin);
    }

    @Test
    void register() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        User newUser = UserUtil.createNewFromTo(newTo);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.getId();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), UserUtil.updateFromTo(new User(user), updatedTo));
    }

    @Test
    void getWithMeals() throws Exception {
        assumeDataJpa();
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-meals")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_WITH_MEALS_MATCHER.contentJson(user));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateEmail() throws Exception {
        UserTo duplicateUser = UserUtil.asTo(new User(admin));
        duplicateUser.setEmail(user.getEmail());
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtil.writeValue(duplicateUser)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(type(DATA_ERROR))
                .andExpect(message("user.duplicateEmail"));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateNotValidPassword() throws Exception {
        User notValidUser = new User(admin);
        notValidUser.setPassword("2");
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtil.writeValue(notValidUser)))
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
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtil.writeValue(notValidUser)))
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
        ResultActions action = perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtil.writeValue(notValidUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerWithDuplicateEmail() throws Exception {
        UserTo newTo = new UserTo(null, "newName", admin.getEmail(), "newPassword", 1500);
        User newUser = UserUtil.createNewFromTo(newTo);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(type(DATA_ERROR))
                .andExpect(message("user.duplicateEmail"));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerNotValidName() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "123456", 2000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(notValidUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerNotValidPassword() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "1", 2000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(notValidUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }

    // https://stackoverflow.com/questions/37406714/cannot-test-expected-exception-when-using-transactional-with-commit
    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerNotValidCaloriesPerDay() throws Exception {
        User notValidUser = new User(null, "", admin.getEmail(), "123456", 20000, Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(notValidUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(type(VALIDATION_ERROR));
    }
}