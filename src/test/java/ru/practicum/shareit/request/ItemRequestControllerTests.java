package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestControllerTests {
    @Autowired
    private ItemRequestController itemRequestController;
    @Autowired
    private UserController userController;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto.builder()
                .description("item request description")
                .build();

        user = User.builder()
                .name("name")
                .email("user@email.com")
                .build();
    }

    @Test
    void createTest() {
        User userCreate = userController.create(user);
        ItemRequestDto itemRequest = itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getById(itemRequest.getId(), userCreate.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.create(1L, itemRequestDto));
    }

    @Test
    void getAllByUserTest() {
        User userCreate = userController.create(user);
        itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getAllByUser(userCreate.getId()).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.getAllByUser(1L));
    }

    @Test
    void getAll() {
        User userCreate = userController.create(user);
        itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(0, itemRequestController.getAll(0, 10, userCreate.getId()).size());
        User user2 = User.builder()
                .name("name2")
                .email("user2@email.com")
                .build();
        User userCreate2 = userController.create(user2);
        assertEquals(1, itemRequestController.getAll(0, 10, userCreate2.getId()).size());
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemRequestController.getAll(0, 10, 1L));
    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(BadRequestException.class, () -> itemRequestController.getAll(-1, 10, 1L));
    }
}
