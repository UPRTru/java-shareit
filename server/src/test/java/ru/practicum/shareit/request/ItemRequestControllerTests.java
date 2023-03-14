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
import ru.practicum.shareit.user.dto.UserDto;

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
    private UserDto user;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto.builder()
                .description("item request description")
                .build();

        user = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();
    }

    @Test
    void createTest() {
        UserDto userCreate = userController.create(user);
        ItemRequestDto itemRequest = itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getById(itemRequest.getId(), userCreate.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.create(1L, itemRequestDto));
    }

    @Test
    void getAllByUserTest() {
        UserDto userCreate = userController.create(user);
        itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getAllByUser(userCreate.getId()).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.getAllByUser(1L));
    }

    @Test
    void getAll() {
        UserDto userCreate = userController.create(user);
        itemRequestController.create(userCreate.getId(), itemRequestDto);
        assertEquals(0, itemRequestController.getAll(0, 10, userCreate.getId()).size());
        UserDto user2 = UserDto.builder()
                .name("name2")
                .email("user2@email.com")
                .build();
        UserDto userCreate2 = userController.create(user2);
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
