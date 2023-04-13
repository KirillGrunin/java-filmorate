package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.exception.WrongIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private int increment = 0;
    private final Validator validator;

    private final UserStorage userStorage;

    @Autowired
    public UserService(Validator validator, @Qualifier("DBUserStorage") UserStorage userStorage) {
        this.validator = validator;
        this.userStorage = userStorage;
    }


    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }


    public User add(final User user) {
        validate(user);
        return userStorage.addUser(user);
    }


    public User update(final User user) {
        validate(user);
        return userStorage.updateUser(user);
    }


    public void addFriend(final String supposedUserId, final String supposedFriendId) {
        User user = getStoredUser(supposedUserId);
        User friend = getStoredUser(supposedFriendId);
        userStorage.addFriend(user.getId(), friend.getId());
    }


    public void deleteFriend(final String supposedUserId, final String supposedFriendId) {
        User user = getStoredUser(supposedUserId);
        User friend = getStoredUser(supposedFriendId);
        userStorage.deleteFriend(user.getId(), friend.getId());
    }


    public Collection<User> getFriends(final String supposedUserId) {
        User user = getStoredUser(supposedUserId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            friends.add(userStorage.getUser(id));
        }
        return friends;
    }


    public Collection<User> getCommonFriends(final String supposedUserId, final String supposedOtherId) {
        User user1 = getStoredUser(supposedUserId);
        User user2 = getStoredUser(supposedOtherId);
        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }


    public User getUser(final String supposedId) {
        return getStoredUser(supposedId);
    }

    private void validate(final User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не задано. Установлено значение {} из поля login", user.getLogin());
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не содержит буквенных символов. " +
                    "Установлено значение {} из поля login", user.getLogin());
        }
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ConstraintViolation<User> userConstraintViolation : violations) {
                messageBuilder.append(userConstraintViolation.getMessage());
            }
            throw new UserValidationException("Ошибка валидации Пользователя: " + messageBuilder, violations);
        }
        if (user.getId() == 0) {
            user.setId(++increment);
        }
    }

    private Integer idFromString(final String supposedId) {
        try {
            return Integer.valueOf(supposedId);
        } catch (NumberFormatException exception) {
            throw new WrongIdException("Некорректный id");
        }
    }

    private User getStoredUser(final String supposedId) {
        final int userId = idFromString(supposedId);
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификатором " +
                    userId + " не зарегистрирован!");
        }
        return user;
    }
}