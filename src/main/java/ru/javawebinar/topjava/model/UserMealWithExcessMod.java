package ru.javawebinar.topjava.model;

import ru.javawebinar.topjava.util.BooleanContainer;

import java.time.LocalDateTime;

public class UserMealWithExcessMod {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final BooleanContainer excess;

    public UserMealWithExcessMod(LocalDateTime dateTime, String description, int calories, BooleanContainer excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public UserMealWithExcessMod(UserMeal userMeal, BooleanContainer excess) {
        this.dateTime = userMeal.getDateTime();
        this.description = userMeal.getDescription();
        this.calories = userMeal.getCalories();
        this.excess = excess;
    }

    @Override
    public String toString() {
        return "UserMealWithExcess{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }
}
