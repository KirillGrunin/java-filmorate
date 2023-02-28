package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private static final LocalDate START_DATA = LocalDate.of(1895, 12, 28);

    public List<Film> getAllFilms() {
        List<Film> filmsList = new ArrayList<>(filmStorage.getFilms().values());
        log.debug("Количество фильмов: {}", filmsList.size());
        return filmsList;
    }

    public Film createFilm(Film film) {
        if (filmStorage.getFilms().containsKey(film.getId())) {
            throw new RuntimeException("Фильм уже есть в базе");
        }
        validateReleaseDate(film, "Добавлен");
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            throw new RuntimeException("Фильм нет в базе");
        }
        validateReleaseDate(film, "Обновлен");
        return filmStorage.update(film);
    }

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore(START_DATA)) {
            throw new ValidationException("Дата релиза не может быть раньше " + START_DATA);
        }
        log.debug("{} фильм: {}", text, film.getName());
    }
}