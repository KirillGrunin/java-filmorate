package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getFilms() {
        List<User> usersList = new ArrayList<>(userService.getAllUsers().values());
        log.debug("Количество пользователей: {}", usersList.size());
        return usersList;
    }

    @PostMapping
    public User createFilm(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateFilm(@RequestBody User user) {
        return userService.updateUser(user);
    }

}