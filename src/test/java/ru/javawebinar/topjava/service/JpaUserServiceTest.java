package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;

@ActiveProfiles(profiles = Profiles.JPA)
public class JpaUserServiceTest extends AbstractUserServiceTest {
}
