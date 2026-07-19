package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Rating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String name;

    Rating(String name) {
        this.name = name;
    }

    @JsonProperty("id")
    public int getId() {
        return this.ordinal() + 1;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public static Rating fromName(String name) {
        for (Rating rating : values()) {
            if (rating.name.equalsIgnoreCase(name)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Неизвестный жанр: " + name);
    }

    public static Rating fromId(int id) {
        int index = id - 1; // Переводим пользовательский ID обратно в индекс Java (0-based)
        if (index < 0 || index >= values().length) {
            throw new IllegalArgumentException("Рейтинг с id " + id + " не найден");
        }
        return values()[index];
    }

    @Override
    public String toString() {
        return name;
    }
}