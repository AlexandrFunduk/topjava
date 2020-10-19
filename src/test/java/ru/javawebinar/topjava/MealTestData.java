package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int MEAL_ID = AbstractBaseEntity.START_SEQ + 2;
    public static final Meal userMeal = new Meal(MEAL_ID, LocalDateTime.of(2020, 10, 17, 7, 0, 0), "Завтрак User", 500);
    public static final Meal userMeal2 = new Meal(MEAL_ID + 1, LocalDateTime.of(2020, 10, 18, 13, 20, 0), "Обед User", 1000);
    public static final Meal userMeal3 = new Meal(MEAL_ID + 2, LocalDateTime.of(2020, 10, 19, 21, 10, 0), "Ужин User", 200);
    public static final Meal adminMeal = new Meal(MEAL_ID + 3, LocalDateTime.of(2020, 10, 18, 7, 0, 0), "Завтрак Admin", 888);
    public static final Meal adminMeal2 = new Meal(MEAL_ID + 4, LocalDateTime.of(2020, 10, 18, 13, 20, 0), "Обед Admin", 999);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 10, 19, 0, 0, 0), "Завтрак", 500);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal);
        updated.setDescription("XXX");
        updated.setCalories(777);
        updated.setDateTime(LocalDateTime.of(2021, 1, 1, 0, 0, 0));
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
