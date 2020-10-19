DELETE
FROM user_roles;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, datetime, description, calories)
VALUES (100000, '20201017 07:00:00', 'Завтрак User', 500),
       (100000, '20201018 13:20:00', 'Обед User', 1000),
       (100000, '20201019 21:10:00', 'Ужин User', 200),
       (100001, '20201018 07:00:00', 'Завтрак Admin', 888),
       (100001, '20201018 13:20:00', 'Обед Admin', 999);