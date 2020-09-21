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
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 8, 0), "Завтрак", 700),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 11, 0), "Обед", 900),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 23, 0), "Ужин", 300)
        );
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


    // stream for unmodified class. Using an external collection.
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

    // Решение для не модифицированного класса с проходом по внутренней коллекции в методе finisher()
    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        return meals.stream()
                .collect(new CollectorForNoEditClass(startTime, endTime, caloriesPerDay));
    }

    //данный коллектор может работать с оригинальной реализацией класса UserMealWithExcess использующей тип boolean
    private static class CollectorForNoEditClass implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExcess>> {
        private final ConcurrentMap<LocalDate, Integer> mapCaloriesPerDay = new ConcurrentHashMap<>();
        private final ConcurrentMap<LocalDate, AtomicBoolean> mapDayExcess = new ConcurrentHashMap<>();
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer caloriesPerDay;

        public CollectorForNoEditClass(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
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
                mapCaloriesPerDay.merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    mapDayExcess.computeIfAbsent(getDate(userMeal), localDate -> new AtomicBoolean(mapCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay));
                    userMeals.add(userMeal);
                }
                if (mapDayExcess.get(userMeal.getDateTime().toLocalDate()) != null) {
                    mapDayExcess.get(userMeal.getDateTime().toLocalDate()).set(mapCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
                }
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
            return userMeals -> userMeals.stream().map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), mapDayExcess.get(userMeal.getDateTime().toLocalDate()).get())).collect(Collectors.toList());
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
        if (meals == null || meals.isEmpty() || (startTime != null && endTime != null && startTime.compareTo(endTime) > 0)) {
            return new ArrayList<>();
        }
        return meals.stream().collect(new CollectorForEditClass(startTime, endTime, caloriesPerDay));
    }

    // Коллектор выполняется в один проход и игнорирует метод finisher() фактически повторяя алгоритм решение filteredByCycles_1
    private static class CollectorForEditClass implements Collector<UserMeal, List<UserMealWithExcessMod>, List<UserMealWithExcessMod>> {
        private final ConcurrentMap<LocalDate, Integer> mapCaloriesPerDay = new ConcurrentHashMap<>();
        private final ConcurrentMap<LocalDate, AtomicBoolean> mapDayExcess = new ConcurrentHashMap<>();
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer caloriesPerDay;

        public CollectorForEditClass(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
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
                mapCaloriesPerDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                    mapDayExcess.putIfAbsent(userMeal.getDateTime().toLocalDate(), new BooleanContainer(false));
                    UserMealWithExcessMod userMealWithExcessMod = new UserMealWithExcessMod(userMeal, mapDayExcess.get(userMeal.getDateTime().toLocalDate()));
                    userMealWithExcesses.add(userMealWithExcessMod);
                }
                if (mapDayExcess.get(userMeal.getDateTime().toLocalDate()) != null) {
                    mapDayExcess.get(userMeal.getDateTime().toLocalDate()).set(mapCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
                }
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
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                    Collector.Characteristics.UNORDERED,
                    Collector.Characteristics.IDENTITY_FINISH));
        }
    }

    private static class Collector3 implements Collector<UserMeal, Container, List<UserMealWithExcessMod>> {
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer caloriesPerDay;
        private final ConcurrentMap<LocalDate, AtomicBoolean> mapDayExcess = new ConcurrentHashMap<>();

        public Collector3(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<Container> supplier() {
            return Container::new;
        }

        @Override
        public BiConsumer<Container, UserMeal> accumulator() {
            return (container, userMeal) -> {
                container.getMapCaloriesPerDay().merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    mapDayExcess.computeIfAbsent(getDate(userMeal), localDate -> new AtomicBoolean(container.getMapCaloriesPerDay().get(getDate(userMeal)) > caloriesPerDay));
                    UserMealWithExcessMod userMealWithExcessMod = createUserMealWithExcessMod(userMeal, mapDayExcess.get(getDate(userMeal)));
                    container.getUserMealWithExcessesMod().add(userMealWithExcessMod);
                }
                if (mapDayExcess.get(getDate(userMeal)) != null && !mapDayExcess.get(getDate(userMeal)).get() && container.getMapCaloriesPerDay().get(getDate(userMeal)) > caloriesPerDay) {
                    mapDayExcess.get(getDate(userMeal)).set(true);
                }
            };
        }

        @Override
        public BinaryOperator<Container> combiner() {
            return (container, container2) -> {
                container.getUserMealWithExcessesMod().addAll(container2.getUserMealWithExcessesMod());
                container2.getMapCaloriesPerDay().forEach((localDate, integer) -> {
                    if (container.getMapCaloriesPerDay().merge(localDate, integer, Integer::sum) > caloriesPerDay) {
                        mapDayExcess.get(localDate).set(true);
                    }
                });
                return container;
            };
        }

        @Override
        public Function<Container, List<UserMealWithExcessMod>> finisher() {
            return Container::getUserMealWithExcessesMod;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                    Collector.Characteristics.UNORDERED,
                    Collector.Characteristics.IDENTITY_FINISH));
        }
    }

    private static class Container {
        private final List<UserMealWithExcessMod> userMealWithExcessesMod;
        private final Map<LocalDate, Integer> mapCaloriesPerDay;

        public Container() {
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
