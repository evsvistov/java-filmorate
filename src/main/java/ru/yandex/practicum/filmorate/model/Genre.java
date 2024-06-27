package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private Long id;   // Уникальный идентификатор жанра
    private String name; // Название жанра

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
