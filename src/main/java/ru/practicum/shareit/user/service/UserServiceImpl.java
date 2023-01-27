package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userStorage.findAll()) {
            users.add(toUserDto(user));
        }

        return users;
    }

    @Override
    public UserDto getById(Long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден."));

        return toUserDto(user);
    }

    @Override
    public UserDto create(User user) {
        throwIfEmailNotUnique(user);

        return toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(User user, Long id) {
        User updatedUser = userStorage.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователь id: " + id + " не найден."));
        if (user.getEmail() != null) {
            throwIfEmailNotUnique(user);
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        return toUserDto(userStorage.update(updatedUser));
    }

    @Override
    public void delete(Long id) {
        getById(id);
        userStorage.delete(id);
    }

    private void throwIfEmailNotUnique(User user) {
        for (User userCheck : userStorage.findAll()) {
            if (user.getEmail().equals(userCheck.getEmail())) {
                throw new ConflictException("Указанный email занят.");
            }
        }
    }
}
