package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealMemoryRepository implements MealRepository {
    private static final AtomicLong index = new AtomicLong(0);
    private final Map<Long, Meal> meals = new ConcurrentHashMap<>();

    public MealMemoryRepository() {
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal findById(Long id) {
        return meals.get(id);
    }

    @Override
    public Meal save(Meal meal) {
        Long id = meal.getId();
        if (id == null || !meals.containsKey(id)) {
            meal.setId(index.getAndAdd(1));
        }
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public void deleteById(Long id) {
        meals.remove(id);
    }
}
