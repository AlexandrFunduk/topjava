package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;
import ru.javawebinar.topjava.model.UserMealWithExcessMod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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
        mealsTo = filteredByStreams2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

    }

    // two cycles for unmodified class
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return result;
        }
        Map<LocalDate, Integer> caloriesPerDays = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesPerDays.merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
        }
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                result.add(createUserMealWithExcess(userMeal, caloriesPerDays.get(getDate(userMeal)) > caloriesPerDay));
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
        Map<LocalDate, Integer> caloriesPerDays = new HashMap<>();
        Map<LocalDate, AtomicBoolean> dayExcess = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate date = getDate(userMeal);
            int currentCaloriesPerDay = caloriesPerDays.merge(date, userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                UserMealWithExcessMod userMealWithExcessMod = createUserMealWithExcessMod(userMeal,
                        dayExcess.computeIfAbsent(date, localDate -> new AtomicBoolean(currentCaloriesPerDay > caloriesPerDay)));
                result.add(userMealWithExcessMod);
            }
            boolean excess = dayExcess.getOrDefault(date, new AtomicBoolean(true)).get();
            if (!excess && currentCaloriesPerDay > caloriesPerDay) {
                dayExcess.get(date).set(true);
            }
        }
        return result;
    }

    // stream for unmodified class. Using an external collection
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        final Map<LocalDate, Boolean> excesses = meals.stream().collect(Collectors.groupingBy(UserMealsUtil::getDate, new CollectorCaloriesToExcess(caloriesPerDay)));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime))
                .map(userMeal -> createUserMealWithExcess(userMeal, excesses.get(getDate(userMeal))))
                .collect(Collectors.toList());
    }

    // collector return excess for one dey
    private static class CollectorCaloriesToExcess implements java.util.stream.Collector<UserMeal, int[], Boolean> {
        private final int caloriesPerDay;

        public CollectorCaloriesToExcess(int caloriesPerDay) {
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<int[]> supplier() {
            return () -> new int[1];
        }

        @Override
        public BiConsumer<int[], UserMeal> accumulator() {
            return (ints, userMeal) -> ints[0] += userMeal.getCalories();
        }

        @Override
        public BinaryOperator<int[]> combiner() {
            return (integers, integers2) -> {
                integers[0] = +integers2[0];
                return integers;
            };
        }

        @Override
        public Function<int[], Boolean> finisher() {
            return integers -> integers[0] > caloriesPerDay;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    // stream for unmodified class
    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors
                        .collectingAndThen(
                                Collectors.groupingBy(
                                        UserMealsUtil::getDate,
                                        new CollectorToUserMealWithExcesses(caloriesPerDay, startTime, endTime)),
                                localDateListMap -> localDateListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList())));

    }

    // collector for unmodified class. Without using an external collection
    private static class CollectorToUserMealWithExcesses implements java.util.stream.Collector<UserMeal, ContainerIntUM, List<UserMealWithExcess>> {
        private final int caloriesPerDay;
        private final LocalTime startTime;
        private final LocalTime endTime;


        private CollectorToUserMealWithExcesses(int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
            this.caloriesPerDay = caloriesPerDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Supplier<ContainerIntUM> supplier() {
            return ContainerIntUM::new;
        }

        @Override
        public BiConsumer<ContainerIntUM, UserMeal> accumulator() {
            return (containerIntUM, userMeal) -> {
                containerIntUM.addCalories(userMeal.getCalories());
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    containerIntUM.addMeal(userMeal);
                }
            };
        }

        @Override
        public BinaryOperator<ContainerIntUM> combiner() {
            return ContainerIntUM::union;
        }

        @Override
        public Function<ContainerIntUM, List<UserMealWithExcess>> finisher() {
            return containerIntUM -> {
                boolean excess = containerIntUM.getCalories() > caloriesPerDay;
                return containerIntUM.getMeals().stream().map(userMeal -> createUserMealWithExcess(userMeal, excess)).collect(Collectors.toList());
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    // container for accumulator
    private static class ContainerIntUM {
        private int calories;
        private final List<UserMeal> meals = new ArrayList<>();

        public int getCalories() {
            return calories;
        }

        public List<UserMeal> getMeals() {
            return meals;
        }

        public void addCalories(Integer calories) {
            this.calories += calories;
        }

        public void addMeal(UserMeal userMeal) {
            meals.add(userMeal);
        }

        public static ContainerIntUM union(ContainerIntUM c1, ContainerIntUM c2) {
            c1.addCalories(c2.getCalories());
            c1.getMeals().addAll(c2.getMeals());
            return c1;
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
