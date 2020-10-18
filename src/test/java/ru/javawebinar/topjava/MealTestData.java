package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int MEAL_ID = 100002;
    public static final Meal meal = new Meal(MEAL_ID, LocalDateTime.of(2020, 10, 17, 7, 0, 0), "Завтрак", 500);
    public static final Meal meal2 = new Meal(100003, LocalDateTime.of(2020, 10, 18, 13, 20, 0), "Обед", 1000);
    public static final Meal meal3 = new Meal(100004, LocalDateTime.of(2020, 10, 18, 7, 0, 0), "Завтрак", 500);
    public static final Meal meal4 = new Meal(100005, LocalDateTime.of(2020, 10, 18, 13, 20, 0), "Обед", 1000);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 10, 19, 0, 0, 0), "Завтрак", 500);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal);
        updated.setDescription("XXX");
        updated.setCalories(777);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}
