--DROP TABLE film
CREATE TABLE film
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR NOT NULL,
    description  VARCHAR(200),
    release_date DATE CHECK (release_date <= CURRENT_DATE AND release_date >= '1895-12-28'),
    duration     BIGINT CHECK (duration > 0),
    mpa_rating   VARCHAR(5) CHECK (mpa_rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

COMMENT
ON TABLE film IS 'Таблица для хранения информации о фильмах';
COMMENT
ON COLUMN film.id IS 'Уникальный идентификатор фильма';
COMMENT
ON COLUMN film.name IS 'Название фильма, не может быть пустым';
COMMENT
ON COLUMN film.description IS 'Описание фильма, максимальная длина — 200 символов';
COMMENT
ON COLUMN film.release_date IS 'Дата релиза фильма, не может быть в будущем и не может быть раньше 28 декабря 1895 года';
COMMENT
ON COLUMN film.duration IS 'Продолжительность фильма в минутах, должна быть положительным числом';
COMMENT
ON COLUMN film.mpa_rating IS 'Рейтинг Ассоциации кинокомпаний, ограничивающий возрастной просмотр';

--DROP TABLE genre
CREATE TABLE genre
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL
);

COMMENT
ON TABLE genre IS 'Таблица для хранения жанров фильмов';
COMMENT
ON COLUMN genre.id IS 'Уникальный идентификатор жанра';
COMMENT
ON COLUMN genre.name IS 'Название жанра';

--DROP TABLE film_genre
CREATE TABLE film_genre
(
    film_id  BIGINT REFERENCES film (id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

COMMENT
ON TABLE film_genre IS 'Таблица для связывания фильмов с их жанрами';
COMMENT
ON COLUMN film_genre.film_id IS 'Идентификатор фильма, внешний ключ';
COMMENT
ON COLUMN film_genre.genre_id IS 'Идентификатор жанра, внешний ключ';

--DROP TABLE "user"
CREATE TABLE "user"
(
    id    BIGSERIAL PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE CHECK (email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'
) ,
    login VARCHAR NOT NULL CHECK (login !~* '\s'),
    name VARCHAR,
    birthday DATE CHECK (birthday <= CURRENT_DATE)
);

COMMENT
ON TABLE "user" IS 'Таблица для хранения информации о пользователях';
COMMENT
ON COLUMN "user".id IS 'Уникальный идентификатор пользователя';
COMMENT
ON COLUMN "user".email IS 'Электронная почта пользователя, должна быть корректной и содержать символ @, уникальное значение';
COMMENT
ON COLUMN "user".login IS 'Логин пользователя, не может быть пустым и содержать пробелы';
COMMENT
ON COLUMN "user".name IS 'Имя пользователя';
COMMENT
ON COLUMN "user".birthday IS 'Дата рождения пользователя, не может быть в будущем';

--DROP TABLE friendship
CREATE TABLE friendship
(
    user_id   BIGINT REFERENCES "user" (id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES "user" (id) ON DELETE CASCADE,
    status    BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, friend_id)
);

COMMENT
ON TABLE friendship IS 'Таблица для хранения информации о дружбе между пользователями';
COMMENT
ON COLUMN friendship.user_id IS 'Идентификатор пользователя, который отправил запрос на дружбу, внешний ключ';
COMMENT
ON COLUMN friendship.friend_id IS 'Идентификатор пользователя, которому отправлен запрос на дружбу, внешний ключ';
COMMENT
ON COLUMN friendship.status IS 'Статус дружбы: TRUE — подтверждённая, FALSE — неподтверждённая';
