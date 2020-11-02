package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;

@ActiveProfiles(profiles = Profiles.JDBC)
public class JdbcMealServiceTest extends AbstractMealServiceTest {
}
