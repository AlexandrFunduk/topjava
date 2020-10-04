package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.LocalRepo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalTime;
import java.util.List;

public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository = LocalRepo.getInstance();

    @Override
    public List<MealTo> findAll() {
        return MealsUtil.filteredByStreams(mealRepository.findAll(), LocalTime.MIN, LocalTime.MAX, 2000);
    }

    @Override
    public MealTo findById(Long id) {
        Meal meal = mealRepository.findById(id);
        List<MealTo> meals = MealsUtil.filteredByStreams(mealRepository.findByDate(meal.getDate()), LocalTime.MIN, LocalTime.MAX, 2000);
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), meals.get(0).isExcess());
    }

    @Override
    public void save(Meal meal) {
        mealRepository.save(meal);
    }

    @Override
    public void save(MealTo mealTo) {
        mealRepository.save(new Meal(mealTo.getDateTime(), mealTo.getDescription(), mealTo.getCalories()));
    }

    @Override
    public void deleteById(Long id) {
        mealRepository.deleteById(id);
    }
}
