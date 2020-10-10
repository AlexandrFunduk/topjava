package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetween;
import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

public abstract class AbstractMealController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll authUserId()=" + authUserId());
        return service.getAll(authUserId(), authUserCaloriesPerDay());
    }

    public List<MealTo> getByFilter(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getByFilter startDate - {}, endDate - {}, startTime - {}, endTime - {}", startDate, endDate, startTime, endTime);
        final LocalDate startD = startDate == null ? LocalDate.MIN : startDate;
        final LocalDate endD = endDate == null ? LocalDate.MAX : endDate;
        final LocalTime startT = startTime == null ? LocalTime.MIN : startTime;
        final LocalTime endT = endTime == null ? LocalTime.MAX : endTime;

        return service.getByFilter(meal ->
                        isBetween(meal.getDate(), startD, endD) && isBetweenHalfOpen(meal.getTime(), startT, endT),
                authUserId(), authUserCaloriesPerDay());
    }

    public Meal get(int id) {
        log.info("getAll");
        return service.get(id, authUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }
}
