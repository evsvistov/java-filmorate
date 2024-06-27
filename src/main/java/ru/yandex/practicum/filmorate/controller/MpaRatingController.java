package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления рейтингами MPA.
 */
@RestController
@RequestMapping("/mpa")
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @Autowired
    public MpaRatingController(MpaRatingService mpaRatingService) {
        this.mpaRatingService = mpaRatingService;
    }

    @GetMapping
    public ResponseEntity<List<MpaRating>> findAll() {
        return ResponseEntity.ok(mpaRatingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> findById(@PathVariable Long id) {
        Optional<MpaRating> mpaRating = mpaRatingService.findById(id);
        return mpaRating.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
