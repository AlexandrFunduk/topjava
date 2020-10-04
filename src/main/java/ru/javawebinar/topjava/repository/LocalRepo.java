package ru.javawebinar.topjava.repository;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.UserServlet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class LocalRepo implements MealRepository {
    private static final Logger log = getLogger(UserServlet.class);
    private static final LocalRepo ourInstance;
    private static volatile Long index = 0L;

    static {
        ourInstance = new LocalRepo();
    }

    private LocalRepo() {
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    public static LocalRepo getInstance() {
        return ourInstance;
    }

    private final ConcurrentMap<Long, Meal> meals = new ConcurrentHashMap<>();

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public List<Meal> findByDate(LocalDate date) {
        return meals.values().stream().filter(meal -> meal.getDate().equals(date)).collect(Collectors.toList());
    }

    @Override
    public Meal findById(Long id) {
        return meals.get(id);
    }

    @Override
    public synchronized Meal save(Meal meal) {
        if (meal.getId().equals(-1L)) {
            meal.setId(index);
            index++;
        }
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public void deleteById(Long id) {
        meals.remove(id);
    }
}
