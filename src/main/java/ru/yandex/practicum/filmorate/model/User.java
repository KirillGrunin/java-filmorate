package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.CorrectLogin;

import java.time.LocalDate;

@Data
public class User {
    @PositiveOrZero(message = "id can not be negative")
    private int id;

    @NotNull(message = "login must not be null")
    @Email(message = "invalid email")
    private String email;

    @NotBlank(message = "login must not be empty")
    @CorrectLogin
    private String login;

    private String name;

    /**
     * Дата рождения должна быть моментом, датой или временем в прошлом или настоящем
     */
    @PastOrPresent
    private LocalDate birthday;
}