package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    @GetMapping
    public List<Rating> findAll() {
        return Arrays.asList(Rating.values());
    }

    @PutMapping("/{id}")
    public Rating findRatingById(@PathVariable int id) {
        try {
            return Rating.fromId(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
    }
}
