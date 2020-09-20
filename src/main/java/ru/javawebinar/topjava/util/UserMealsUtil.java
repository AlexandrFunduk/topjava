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
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 8, 0), "Завтрак", 700),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 11, 0), "Обед", 900),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 23, 0), "Ужин", 300)
        );
        List<UserMeal> list = new ArrayList<>(meals);
        for (int i = 0; i < 10000000; i++) {
            list.addAll(meals);
        }
        List<UserMeal> list10_000 = list.subList(0,10_000);
        List<UserMeal> list100_000 = list.subList(0,100_000);
        List<UserMeal> list1_000_000 = list.subList(0,1_000_000);
        List<UserMeal> list10_000_000 = list.subList(0,10_000_000);

        System.out.println("Test filteredByCycles()");
        testFilteredByCycles(meals, "========Test for meals.size() = 10", false);
        testFilteredByCycles(list10_000, "\n========Test for meals.size() = 10_000", false);
        testFilteredByCycles(list100_000, "\n========Test for meals.size() = 100_000", false);
        testFilteredByCycles(list1_000_000, "\n========Test for meals.size() = 1_000_000", false);
        testFilteredByCycles(list10_000_000, "\n========Test for meals.size() = 10_000_000", false);
        System.out.println();
        System.out.println();
        System.out.println("Test testFilteredByCycles_2()");
        testFilteredByCycles_2(meals, "========Test for meals.size() = 10", false);
        testFilteredByCycles_2(list10_000, "\n========Test for meals.size() = 10_000", false);
        testFilteredByCycles_2(list100_000, "\n========Test for meals.size() = 100_000", false);
        testFilteredByCycles_2(list1_000_000, "\n========Test for meals.size() = 1_000_000", false);
        testFilteredByCycles_2(list10_000_000, "\n========Test for meals.size() = 10_000_000", false);
        System.out.println();
        System.out.println();
        System.out.println("Test testFilteredByStreams()");
        testFilteredByStreams(meals, "========Test for meals.size() = 10", false);
        testFilteredByStreams(list10_000, "\n========Test for meals.size() = 10_000", false);
        testFilteredByStreams(list100_000, "\n========Test for meals.size() = 100_000", false);
        testFilteredByStreams(list1_000_000, "\n========Test for meals.size() = 1_000_000", false);
        testFilteredByStreams(list10_000_000, "\n========Test for meals.size() = 10_000_000", false);
        System.out.println();
        System.out.println();
        System.out.println("Test testFilteredByStreams_2()");
        testFilteredByStreams_2(meals, "========Test for meals.size() = 10", false);
        testFilteredByStreams_2(list10_000, "\n========Test for meals.size() = 10_000", false);
        testFilteredByStreams_2(list100_000, "\n========Test for meals.size() = 100_000", false);
        testFilteredByStreams_2(list1_000_000, "\n========Test for meals.size() = 1_000_000", false);
        testFilteredByStreams_2(list10_000_000, "\n========Test for meals.size() = 10_000_000", false);
        System.out.println();
        System.out.println();
        System.out.println("Test testFilteredByStreams_3()");
        testFilteredByStreams_3(meals, "========Test for meals.size() = 10", false);
        testFilteredByStreams_3(list10_000, "\n========Test for meals.size() = 10_000", false);
        testFilteredByStreams_3(list100_000, "\n========Test for meals.size() = 100_000", false);
        testFilteredByStreams_3(list1_000_000, "\n========Test for meals.size() = 1_000_000", false);
        testFilteredByStreams_3(list10_000_000, "\n========Test for meals.size() = 10_000_000", false);
    }
    private static void testFilteredByCycles(List<UserMeal> meals, String message, boolean resultToConsole) {
        System.out.print(message);
        long startTime = System.nanoTime();
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.print(" Time " + (System.nanoTime()-startTime)/1000000000.0 + ";");
        if (resultToConsole) mealsTo.forEach(System.out::println);
    }
    private static void testFilteredByCycles_2(List<UserMeal> meals, String message, boolean resultToConsole) {
        System.out.print(message);
        long startTime = System.nanoTime();
        List<UserMealWithExcessMod> mealsTo = filteredByCycles_2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.print(" Time " + (System.nanoTime()-startTime)/1000000000.0 + ";");
        if (resultToConsole) mealsTo.forEach(System.out::println);
    }
    private static void testFilteredByStreams(List<UserMeal> meals, String message, boolean resultToConsole) {
        System.out.print(message);
        long startTime = System.nanoTime();
        List<UserMealWithExcess> mealsTo1 = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.print(" Time " + (System.nanoTime()-startTime)/1000000000.0 + ";");
        if (resultToConsole) mealsTo1.forEach(System.out::println);
    }
    private static void testFilteredByStreams_2(List<UserMeal> meals, String message, boolean resultToConsole) {
        System.out.print(message);
        long startTime = System.nanoTime();
        List<UserMealWithExcess> mealsTo2 = filteredByStreams_2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.print(" Time " + (System.nanoTime()-startTime)/1000000000.0 + ";");
        if (resultToConsole) mealsTo2.forEach(System.out::println);
    }
    private static void testFilteredByStreams_3(List<UserMeal> meals, String message, boolean resultToConsole) {
        System.out.print(message);
        long startTime = System.nanoTime();
        List<UserMealWithExcessMod> mealsTo4 = filteredByStreams_3(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.print(" Time " + (System.nanoTime()-startTime)/1000000000.0 + ";");
        if (resultToConsole) mealsTo4.forEach(System.out::println);
    }

    // решение для не модифицированного класса с дополнительным проходом по отфильтрованному по времени и excess = false списку
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

    //    Решение для модифицированного класса в один проход с помощью цикла
    public static List<UserMealWithExcessMod> filteredByCycles_2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcessMod> result = new ArrayList<>();
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return result;
        }
        Map<LocalDate, Integer> map = new HashMap<>();
        Map<LocalDate, BooleanContainer> mapBool = new HashMap<>();
        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                mapBool.putIfAbsent(userMeal.getDateTime().toLocalDate(), new BooleanContainer(false));
                UserMealWithExcessMod userMealWithExcessMod = new UserMealWithExcessMod(userMeal, mapBool.get(userMeal.getDateTime().toLocalDate()));
                result.add(userMealWithExcessMod);
            }
            if (mapBool.get(userMeal.getDateTime().toLocalDate()) != null)
                mapBool.get(userMeal.getDateTime().toLocalDate()).set(map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
        }
        return result;
    }


    //    Решение для не модифицированного класса с использованием внешней коллекции
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

    // Решение для не модифицированного класса с проходом по внутренней коллекции в методе finisher()
    public static List<UserMealWithExcess> filteredByStreams_2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return new ArrayList<>();
        }
        return meals.stream().collect(new collectorForNoEditClass(startTime, endTime, caloriesPerDay));
    }

    //данный коллектор может работать с оригинальной реализацией класса UserMealWithExcess использующей тип boolean
    private static class collectorForNoEditClass implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExcess>> {
        ConcurrentMap<LocalDate, Integer> map = new ConcurrentHashMap<>();
        ConcurrentMap<LocalDate, BooleanContainer> mapBool = new ConcurrentHashMap<>();
        LocalTime startTime;
        LocalTime endTime;
        Integer caloriesPerDay;

        public collectorForNoEditClass(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<List<UserMeal>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<UserMeal>, UserMeal> accumulator() {
            return (userMeals, userMeal) -> {
                map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                    mapBool.putIfAbsent(userMeal.getDateTime().toLocalDate(), new BooleanContainer(false));
                    userMeals.add(userMeal);
                }
                if (mapBool.get(userMeal.getDateTime().toLocalDate()) != null)
                    mapBool.get(userMeal.getDateTime().toLocalDate()).set(map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
            };
        }

        @Override
        public BinaryOperator<List<UserMeal>> combiner() {
            return (userMeals, userMeals1) -> {
                userMeals.addAll(userMeals1);
                return userMeals;
            };
        }

        @Override
        public Function<List<UserMeal>, List<UserMealWithExcess>> finisher() {
//           в данном случае несогласованность условий:
//           "нельзя 2 раза проходить по исходному списку (в том числе его отфильтрованной копии)"
//           "нельзя использовать внешние коллекции, не являющиеся частью коллектора"
//           ставит в затруднительное положение.
//           Является в данном случае List<UserMeal> отфильтрованной копией исходного списка или это внутренняя коллекция коллектора?
//           Если это отфильтрованная копия, то останется она таковой, если я приведу ее к виду List<UserMeal[]>?
            return userMeals -> userMeals.stream().map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), mapBool.get(userMeal.getDateTime().toLocalDate()).get())).collect(Collectors.toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                    Collector.Characteristics.UNORDERED));
        }
    }

    // Решение для модифицированного класса в один проход с использованием коллектора без метода finisher()
    // (java.lang.OutOfMemoryError: GC overhead limit exceeded при meals.size() = 100000000)
    public static List<UserMealWithExcessMod> filteredByStreams_3(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || startTime == null || endTime == null || meals.isEmpty() || startTime.compareTo(endTime) > 0) {
            return new ArrayList<>();
        }
        return meals.stream().collect(new collectorForEditClass(startTime, endTime, caloriesPerDay));
    }

    // Коллектор выполняется в один проход и игнорирует метод finisher() фактически повторяя алгоритм решение filteredByCycles_1
    private static class collectorForEditClass implements Collector<UserMeal, List<UserMealWithExcessMod>, List<UserMealWithExcessMod>> {
        ConcurrentMap<LocalDate, Integer> map = new ConcurrentHashMap<>();
        ConcurrentMap<LocalDate, BooleanContainer> mapBool = new ConcurrentHashMap<>();
        LocalTime startTime;
        LocalTime endTime;
        Integer caloriesPerDay;

        public collectorForEditClass(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<List<UserMealWithExcessMod>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<UserMealWithExcessMod>, UserMeal> accumulator() {
            return (userMealWithExcesses, userMeal) -> {
                map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                    mapBool.putIfAbsent(userMeal.getDateTime().toLocalDate(), new BooleanContainer(false));
                    UserMealWithExcessMod userMealWithExcessMod = new UserMealWithExcessMod(userMeal, mapBool.get(userMeal.getDateTime().toLocalDate()));
                    userMealWithExcesses.add(userMealWithExcessMod);
                }
                if (mapBool.get(userMeal.getDateTime().toLocalDate()) != null)
                    mapBool.get(userMeal.getDateTime().toLocalDate()).set(map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
            };
        }

        @Override
        public BinaryOperator<List<UserMealWithExcessMod>> combiner() {
            return (userMealWithExcesses, userMealWithExcesses2) -> {
                userMealWithExcesses.addAll(userMealWithExcesses2);
                return userMealWithExcesses;
            };
        }

        @Override
        public Function<List<UserMealWithExcessMod>, List<UserMealWithExcessMod>> finisher() {
            return userMealWithExcesses -> userMealWithExcesses;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(/*Collector.Characteristics.CONCURRENT,*/
                    Collector.Characteristics.UNORDERED,
                    Collector.Characteristics.IDENTITY_FINISH));
        }
    }

}
