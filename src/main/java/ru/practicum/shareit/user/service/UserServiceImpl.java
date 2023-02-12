package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(toUserDto(user));
        }
        return users;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден."));
        return toUserDto(user);
    }

    @Transactional
    @Override
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            boolean email;
            try {
                List<User> users = userRepository.findUserByEmail(user.getEmail());
                email = users.size() == 0;
            } catch (Exception exception) {
                email = true;
            }
            if (email) throw new ConflictException("Email занят.");
            throw new BadRequestException("Данные введены неверно или заняты.");
        }
    }

    @Transactional
    @Override
    public UserDto update(User user, Long id) {
        User updatedUser = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователь id: " + id + " не найден."));
        if (user.getEmail() != null && !user.getEmail().equals("")) {
            if (!userRepository.findAllByIdNotAndEmail(id, user.getEmail()).isEmpty()) {
                throw new ConflictException("Email занят.");
            }
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().equals("")) {
            if (!userRepository.findAllByIdNotAndName(id, user.getName()).isEmpty()) {
                throw new ConflictException("Имя занято.");
            }
            updatedUser.setName(user.getName());
        }
        return toUserDto(userRepository.save(updatedUser));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
