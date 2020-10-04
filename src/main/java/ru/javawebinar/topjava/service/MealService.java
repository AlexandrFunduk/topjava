package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface MealService {
    List<MealTo> findAll();

    MealTo findById(Long id);

    void save(Meal meal);

    void deleteById(Long id);
}
