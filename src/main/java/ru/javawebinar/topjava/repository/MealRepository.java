package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    List<Meal> findAll();

    Meal findById(Long id);

    Meal save(Meal meal);

    void deleteById(Long id);
}
