package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealMemoryRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalTime;
import java.util.List;

public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository = new MealMemoryRepository();

    @Override
    public List<MealTo> findAll() {
        return MealsUtil.filteredByStreams(mealRepository.findAll(), LocalTime.MIN, LocalTime.MAX, 2000);
    }

    @Override
    public MealTo findById(Long id) {
        Meal meal = mealRepository.findById(id);
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), false);
    }

    @Override
    public void save(Meal meal) {
        mealRepository.save(meal);
    }

    @Override
    public void deleteById(Long id) {
        mealRepository.deleteById(id);
    }
}
