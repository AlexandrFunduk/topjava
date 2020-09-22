package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;
import ru.javawebinar.topjava.model.UserMealWithExcessMod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
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

        System.out.println(">>filteredByCycles");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(">>filteredByCycles2");
        List<UserMealWithExcessMod> mealsToMod = filteredByCycles2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToMod.forEach(System.out::println);
        System.out.println(">>filteredByStreams");
        mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(">>filteredByStreams2");
        mealsToMod = filteredByStreams2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToMod.forEach(System.out::println);
    }

    // two cycles for unmodified class
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return result;
        }
        Map<LocalDate, Integer> map = new HashMap<>();
        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                result.add(createUserMealWithExcess(userMeal, map.get(getDate(userMeal)) > caloriesPerDay));
            }
        }
        return result;
    }

    // one cycle for modified class
    public static List<UserMealWithExcessMod> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcessMod> result = new ArrayList<>();
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return result;
        }
        Map<LocalDate, Integer> mapCaloriesPerDay = new HashMap<>();
        Map<LocalDate, AtomicBoolean> mapDayExcess = new HashMap<>();
        for (UserMeal userMeal : meals) {
            mapCaloriesPerDay.merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                mapDayExcess.computeIfAbsent(getDate(userMeal), localDate -> new AtomicBoolean(mapCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay));
                UserMealWithExcessMod userMealWithExcessMod = createUserMealWithExcessMod(userMeal, mapDayExcess.get(getDate(userMeal)));
                result.add(userMealWithExcessMod);
            }
            if (mapDayExcess.get(getDate(userMeal)) != null && !mapDayExcess.get(getDate(userMeal)).get() && mapCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay) {
                mapDayExcess.get(getDate(userMeal)).set(true);
            }
        }
        return result;
    }

    // stream for unmodified class. Using an external collection
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        final Map<LocalDate, Integer> mapCaloriesPerDay = meals.stream()
                .collect(Collectors.groupingBy(UserMealsUtil::getDate, Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime))
                .map(userMeal -> createUserMealWithExcess(userMeal, mapCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    // stream for modified class. Using custom collector without using an external collection.
    public static List<UserMealWithExcessMod> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        return meals.stream()
                .collect(new CollectorUserMealToUserMealWithExcessMod(startTime, endTime, caloriesPerDay));
    }

    // collector for modified class. Without using an external collection.
    private static class CollectorUserMealToUserMealWithExcessMod implements Collector<UserMeal, ContainerForAccumulator, List<UserMealWithExcessMod>> {
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer caloriesPerDay;
        private final ConcurrentMap<LocalDate, AtomicBoolean> mapDayExcess = new ConcurrentHashMap<>();

        public CollectorUserMealToUserMealWithExcessMod(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<ContainerForAccumulator> supplier() {
            return ContainerForAccumulator::new;
        }

        @Override
        public BiConsumer<ContainerForAccumulator, UserMeal> accumulator() {
            return (accumulator, userMeal) -> {
                accumulator.getMapCaloriesPerDay().merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    mapDayExcess.computeIfAbsent(getDate(userMeal), localDate -> new AtomicBoolean(accumulator.getMapCaloriesPerDay().get(getDate(userMeal)) > caloriesPerDay));
                    UserMealWithExcessMod userMealWithExcessMod = createUserMealWithExcessMod(userMeal, mapDayExcess.get(getDate(userMeal)));
                    accumulator.getUserMealWithExcessesMod().add(userMealWithExcessMod);
                }
                if (mapDayExcess.get(getDate(userMeal)) != null && !mapDayExcess.get(getDate(userMeal)).get() && accumulator.getMapCaloriesPerDay().get(getDate(userMeal)) > caloriesPerDay) {
                    mapDayExcess.get(getDate(userMeal)).set(true);
                }
            };
        }

        @Override
        public BinaryOperator<ContainerForAccumulator> combiner() {
            return (accumulator, accumulator2) -> {
                accumulator.getUserMealWithExcessesMod().addAll(accumulator2.getUserMealWithExcessesMod());
                accumulator2.getMapCaloriesPerDay().forEach((localDate, integer) -> {
                    if (accumulator.getMapCaloriesPerDay().merge(localDate, integer, Integer::sum) > caloriesPerDay) {
                        mapDayExcess.get(localDate).set(true);
                    }
                });
                return accumulator;
            };
        }

        @Override
        public Function<ContainerForAccumulator, List<UserMealWithExcessMod>> finisher() {
            return ContainerForAccumulator::getUserMealWithExcessesMod;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                    Collector.Characteristics.UNORDERED));
        }
    }

    private static class ContainerForAccumulator {
        private final List<UserMealWithExcessMod> userMealWithExcessesMod;
        private final Map<LocalDate, Integer> mapCaloriesPerDay;

        public ContainerForAccumulator() {
            this.userMealWithExcessesMod = new ArrayList<>();
            this.mapCaloriesPerDay = new HashMap<>();
        }

        public List<UserMealWithExcessMod> getUserMealWithExcessesMod() {
            return userMealWithExcessesMod;
        }

        public Map<LocalDate, Integer> getMapCaloriesPerDay() {
            return mapCaloriesPerDay;
        }
    }

    public static LocalTime getTime(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalTime();
    }

    public static LocalDate getDate(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalDate();
    }

    public static UserMealWithExcess createUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

    public static UserMealWithExcessMod createUserMealWithExcessMod(UserMeal userMeal, AtomicBoolean excess) {
        return new UserMealWithExcessMod(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}
