package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Mpa> findAllMpa() {
        return mpaDbStorage.findAllMpa();
    }

    public Mpa findMpaById(int id) {
        return mpaDbStorage.findMpaById(id).orElseThrow(() ->
                new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }
}
