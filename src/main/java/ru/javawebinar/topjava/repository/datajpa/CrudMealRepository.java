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

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    Meal getMealByIdAndUser(Integer integer, User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal meal WHERE meal.id =:id AND meal.user.id=:user_id")
    int delete(@Param("id") int id, @Param("user_id") int userId);

    @Query("SELECT m FROM Meal m WHERE m.user.id=:userId AND m.dateTime >= :startDateTime AND m.dateTime < :endDateTime ORDER BY m.dateTime DESC")
    List<Meal> getBetweenHalfOpen(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("userId") int userId);

    List<Meal> findAllByUserOrderByDateTimeDesc(User user);

    @Query("SELECT meal FROM Meal meal JOIN FETCH meal.user WHERE meal.id = (:id) AND meal.user.id= (:userId)")
    Meal getWithUser(@Param("id") int id, @Param("userId") int userId);
}
