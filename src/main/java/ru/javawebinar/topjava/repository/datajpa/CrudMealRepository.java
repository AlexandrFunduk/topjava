package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    Meal getMealByIdAndUser(Integer integer, User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal meal WHERE meal.id =:id AND meal.user.id=:user_id")
    int delete(@Param("id") int id, @Param("user_id") int userId);

    List<Meal> findAllByUserAndDateTimeGreaterThanEqualAndDateTimeLessThanOrderByDateTimeDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Meal> findAllByUserOrderByDateTimeDesc(User user);
}
