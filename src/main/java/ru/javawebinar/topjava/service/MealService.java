package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

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
        List<Meal> result = repository.getAll(userId);
        return result == null ? Collections.emptyList() : MealsUtil.getTos(result, caloriesPerDay);
    }

    public List<MealTo> getByFilter(int userId, int caloriesPerDay, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        final LocalDate startD = startDate == null ? LocalDate.MIN : startDate;
        final LocalDate endD = endDate == null ? LocalDate.MAX : endDate.plusDays(1);
        final LocalTime startT = startTime == null ? LocalTime.MIN : startTime;
        final LocalTime endT = endTime == null ? LocalTime.MAX : endTime;

        List<Meal> result = repository.getByFilter(userId, startD, endD);
        return result == null ? Collections.emptyList() : MealsUtil.getFilteredTos(result, caloriesPerDay, startT, endT);
    }

}