package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractCacheableUserServiceTest;

import static ru.javawebinar.topjava.Profiles.JPA;
import static ru.javawebinar.topjava.Profiles.TEST;

@ActiveProfiles({JPA, TEST})
public class JpaUserServiceTest extends AbstractCacheableUserServiceTest {
}