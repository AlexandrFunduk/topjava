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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            AtomicBoolean excess = dayExcess.computeIfAbsent(date, localDate -> new AtomicBoolean(currentCaloriesPerDay > caloriesPerDay));
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                UserMealWithExcessMod userMealWithExcessMod = createUserMealWithExcessMod(userMeal, excess);
                result.add(userMealWithExcessMod);
            }
            if (!excess.get() && currentCaloriesPerDay > caloriesPerDay) {
                excess.set(true);
            }
        }
        return result;
    }

    // stream for unmodified class. Using an external collection
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        final Map<LocalDate, Integer> caloriesPerDays = meals.stream()
                .collect(Collectors.groupingBy(UserMealsUtil::getDate, Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime))
                .map(userMeal -> createUserMealWithExcess(userMeal, caloriesPerDays.get(getDate(userMeal)) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    // stream for unmodified class
    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // collector for unmodified class. Without using an external collection
        class CollectorToUserMealWithExcesses implements Collector<UserMeal, AbstractMap.SimpleEntry<List<UserMeal>, Integer>, Stream<UserMealWithExcess>> {
            @Override
            public Supplier<AbstractMap.SimpleEntry<List<UserMeal>, Integer>> supplier() {
                return () -> new AbstractMap.SimpleEntry<>(new ArrayList<>(), 0);
            }

            @Override
            public BiConsumer<AbstractMap.SimpleEntry<List<UserMeal>, Integer>, UserMeal> accumulator() {
                return (listIntegerSimpleEntry, userMeal) -> {
                    listIntegerSimpleEntry.setValue(listIntegerSimpleEntry.getValue() + userMeal.getCalories());
                    if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                        listIntegerSimpleEntry.getKey().add(userMeal);
                    }
                };
            }

            @Override
            public BinaryOperator<AbstractMap.SimpleEntry<List<UserMeal>, Integer>> combiner() {
                return (listIntegerSimpleEntry, listIntegerSimpleEntry2) -> {
                    listIntegerSimpleEntry.getKey().addAll(listIntegerSimpleEntry2.getKey());
                    listIntegerSimpleEntry.setValue(listIntegerSimpleEntry.getValue() + listIntegerSimpleEntry2.getValue());
                    return listIntegerSimpleEntry;
                };
            }

            @Override
            public Function<AbstractMap.SimpleEntry<List<UserMeal>, Integer>, Stream<UserMealWithExcess>> finisher() {
                return listIntegerSimpleEntry -> listIntegerSimpleEntry.getKey().stream().map(userMeal -> createUserMealWithExcess(userMeal, listIntegerSimpleEntry.getValue() > caloriesPerDay));
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        }
        return meals.parallelStream().collect(Collectors
                .collectingAndThen(Collectors.groupingBy(
                        UserMealsUtil::getDate, new CollectorToUserMealWithExcesses()),
                        localDateListMap -> localDateListMap.values().stream().flatMap(userMealWithExcessStream -> userMealWithExcessStream).collect(Collectors.toList())));
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
