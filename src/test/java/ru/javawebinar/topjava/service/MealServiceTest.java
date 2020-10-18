package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() throws Exception {
        Meal meal = service.get(MEAL_ID, UserTestData.USER_ID);
        assertMatch(meal, MealTestData.meal);
    }

    @Test
    public void getNotFound() throws Exception {
        Assert.assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, UserTestData.ADMIN_ID));
    }

    @Test
    public void delete() throws Exception {
        service.delete(MEAL_ID, UserTestData.USER_ID);
        Assert.assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, UserTestData.USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        Assert.assertThrows(NotFoundException.class, () -> service.delete(MEAL_ID, UserTestData.ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() throws Exception {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2020, 10, 17),
                LocalDate.of(2020, 10, 17),
                UserTestData.USER_ID);
        assertMatch(all, meal);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> all = service.getAll(UserTestData.USER_ID);
        assertMatch(all, meal2, meal);
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, UserTestData.USER_ID);
        assertMatch(service.get(MEAL_ID, UserTestData.USER_ID), updated);
    }

    @Test
    public void updateNotFound() throws Exception {
        Meal updated = getUpdated();
        Assert.assertThrows(NotFoundException.class, () -> service.update(updated, UserTestData.ADMIN_ID));
    }

    @Test
    public void create() throws Exception {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, UserTestData.USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, UserTestData.USER_ID), newMeal);
    }
}