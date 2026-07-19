package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION_MOVIE("Боевик");

    private final String name;

    Genre(String name) {
        this.name = name;
    }

    @JsonValue
    public String getTitle() {
        return name;

    }

    @JsonProperty("id")
    public int getId() {
        return this.ordinal() + 1;
    }

    public static Genre fromName(String name) {
        for (Genre genre : values()) {
            if (genre.name.equalsIgnoreCase(name)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Неизвестный жанр: " + name);
    }

    @Override
    public String toString() {
        return name;
    }
}
