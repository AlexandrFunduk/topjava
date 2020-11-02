package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private final EntityManager em;

    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(EntityManager em, CrudMealRepository crudRepository) {
        this.em = em;
        this.crudRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(em.getReference(User.class, userId));
        if (meal.isNew() || get(meal.id(), userId) != null) {
            return crudRepository.save(meal);
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.getMealByIdAndUser(id, em.getReference(User.class, userId));
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAllByUserOrderByDateTimeDesc(em.getReference(User.class, userId));
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findAllByUserAndDateTimeGreaterThanEqualAndDateTimeLessThanOrderByDateTimeDesc(em.getReference(User.class, userId), startDateTime, endDateTime);
    }

    @Override
    public Meal getWithUser(int id, int userId) {
        return crudRepository.getWithUser(id, userId);
    }
}
