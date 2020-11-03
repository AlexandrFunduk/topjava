package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;


@ActiveProfiles(profiles = Profiles.DATAJPA)
public class DataJpaMealServiceTest extends AbstractMealServiceTest {

    @Test
    public void getMealWithUser() {
        Meal actual = service.getMealWithUser(ADMIN_MEAL_ID, ADMIN_ID);
        MEAL_MATCHER.assertMatch(actual, adminMeal1);
        UserTestData.USER_MATCHER.assertMatch(actual.getUser(), admin);
    }

    @Test
    public void getNotFoundMealWithUser() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(UserTestData.NOT_FOUND, USER_ID));
    }
}
