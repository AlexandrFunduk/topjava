package ru.javawebinar.topjava.service.datajpa;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(profiles = Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getUserWithMeals() {
        User user = service.getWithMeals(USER_ID);
        Assertions.assertThat(user.getMeals().size()).isEqualTo(7);
        user = service.getWithMeals(ADMIN_ID);
        Assertions.assertThat(user.getMeals().size()).isEqualTo(2);
    }
}
