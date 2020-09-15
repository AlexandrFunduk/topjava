package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(">>");
        List<UserMealWithExcess> mealsTo1 = filteredByCycles_1(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo1.forEach(System.out::println);
        System.out.println(">>");
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(">>");
        System.out.println(filteredByStreams_1(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return result;
        }
        List<UserMeal> filteredUserMeal = new ArrayList<>();
        Map<LocalDate, Integer> map = new HashMap<>();
        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                if (map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay) {
                    result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
                } else {
                    filteredUserMeal.add(userMeal);
                }
            }
        }
        for (UserMeal userMeal : filteredUserMeal) {
            result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay));
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return new ArrayList<>();
        }
        final Map<LocalDate, Integer> map = meals.stream().collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMeal1 -> TimeUtil.isBetweenHalfOpen(userMeal1.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal1 -> new UserMealWithExcess(userMeal1.getDateTime(), userMeal1.getDescription(), userMeal1.getCalories(), map.get(userMeal1.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCycles_1(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return result;
        }
        Map<LocalDate, Integer> map = new HashMap<>();
        Map<LocalDate, Boolean[]> mapBool = new HashMap<>();
        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
                mapBool.put(userMeal.getDateTime().toLocalDate(), userMealWithExcess.getExcess());
                result.add(userMealWithExcess);
            }
            if (mapBool.get(userMeal.getDateTime().toLocalDate()) != null)
                mapBool.get(userMeal.getDateTime().toLocalDate())[0] = map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
        }
        return result;
    }


    public static List<UserMealWithExcess> filteredByStreams_1(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return new ArrayList<>();
        }
        return meals.stream().collect(new MyCollector(startTime, endTime, caloriesPerDay));
    }

    private static class MyCollector implements Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>> {
        ConcurrentMap<LocalDate, Integer> map = new ConcurrentHashMap<>();
        ConcurrentMap<LocalDate, Boolean[]> mapBool = new ConcurrentHashMap<>();
        LocalTime startTime;
        LocalTime endTime;
        Integer caloriesPerDay;

        public MyCollector(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<List<UserMealWithExcess>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
            return (userMealWithExcesses, userMeal) -> {
                map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                    UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
                    mapBool.put(userMeal.getDateTime().toLocalDate(), userMealWithExcess.getExcess());
                    userMealWithExcesses.add(userMealWithExcess);
                }
                if (mapBool.get(userMeal.getDateTime().toLocalDate()) != null)
                    mapBool.get(userMeal.getDateTime().toLocalDate())[0] = map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
            };
        }

        @Override
        public BinaryOperator<List<UserMealWithExcess>> combiner() {
            return (userMealWithExcesses, userMealWithExcesses2) -> {
                userMealWithExcesses.addAll(userMealWithExcesses2);
                return userMealWithExcesses;
            };
        }

        @Override
        public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
            return userMealWithExcesses -> userMealWithExcesses;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                    Collector.Characteristics.UNORDERED,
                    Collector.Characteristics.IDENTITY_FINISH));
        }
    }
}
