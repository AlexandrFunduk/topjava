package ru.javawebinar.topjava.service.datajpa;

import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles({DATAJPA})
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, UserTestData.admin);
        MealTestData.MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.mealsAdmin);
    }

    @Test
    public void getWithMealsNotFound() {
        Assert.assertThrows(NotFoundException.class,
                () -> service.getWithMeals(1));
    }

    @Test
    public void cache() {
        long statisticHitBefore = sessionFactory.getStatistics().getSecondLevelCacheHitCount();
        service.get(100000);
        service.get(100000);
        long statisticHitAfter = sessionFactory.getStatistics().getSecondLevelCacheHitCount();
        Assertions.assertThat(statisticHitAfter - statisticHitBefore).isEqualTo(0L);
    }
}