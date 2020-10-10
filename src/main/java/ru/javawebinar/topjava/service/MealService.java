package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public Meal update(Meal meal, int userId) {
        return checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public List<MealTo> getAll(int userId, int caloriesPerDay) {
        List<Meal> result = (List<Meal>) repository.getAll(userId);
        return getTos(result, caloriesPerDay);
    }

    public List<MealTo> getByFilter(Predicate<Meal> filter, int userId, int caloriesPerDay) {
        List<Meal> result = (List<Meal>) repository.getByFilter(userId, filter);
        return getTos(result, caloriesPerDay);
    }

    private List<MealTo> getTos(List<Meal> result, int caloriesPerDay) {
        return result == null ? Collections.emptyList() : MealsUtil.getTos(result, caloriesPerDay);
    }
}