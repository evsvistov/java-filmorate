package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRatingDao {

    List<MpaRating> findAll();

    Optional<MpaRating> findById(Long id);

}
