package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTests {
    @Autowired
    private UserController userController;
    private UserDto user;

    @BeforeEach
    void init() {
        user = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();
    }

    @Test
    void createTest() {
        UserDto userCreate = userController.create(user);
        assertEquals(userCreate.getId(), userController.getById(userCreate.getId()).getId());
    }

    @Test
    void updateTest() {
        userController.create(user);
        UserDto userCreate = user.toBuilder().name("update_name").email("update@email.com").build();
        userController.update(userCreate, 1L);
        assertEquals(userCreate.getEmail(), userController.getById(1L).getEmail());
    }

    @Test
    void updateByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> userController.update(user, 1L));
    }

    @Test
    void deleteTest() {
        UserDto userCreate = userController.create(user);
        assertEquals(1, userController.getAll().size());
        userController.delete(userCreate.getId());
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NotFoundException.class, () -> userController.getById(1L));
    }
}

