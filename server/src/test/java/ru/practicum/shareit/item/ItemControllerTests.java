package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingItemAndUserId;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTests {
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemRequestController itemRequestController;
    private ItemDto itemDto;
    private UserDto user;
    private UserDto user1;
    private ItemRequestDto itemRequestDto;
    private CommentDto comment;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        user = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();
        user1 = UserDto.builder()
                .name("name")
                .email("user1@email.com")
                .build();
        itemRequestDto = ItemRequestDto
                .builder()
                .description("item request description")
                .build();
        comment = CommentDto
                .builder()
                .text("first comment")
                .build();
    }

    @Test
    void createTest() {
        UserDto userCreate = userController.create(user);
        ItemDto item = itemController.create(1L, itemDto);
        assertEquals(item.getId(), itemController.getById(item.getId(), userCreate.getId()).getId());
    }

    @Test
    void createWithRequestTest() {
        UserDto userCreate = userController.create(user);
        itemRequestController.create(userCreate.getId(), itemRequestDto);
        itemDto.setRequestId(1L);
        UserDto userCreate1 = userController.create(user1);
        ItemDto item = itemController.create(userCreate1.getId(), itemDto);
        assertEquals(item.getRequestId(), itemController.getById(item.getId(), userCreate1.getId()).getRequestId());
    }

    @Test
    void createByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.create(1L, itemDto));
    }

    @Test
    void createWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        userController.create(user);
        assertThrows(NotFoundException.class, () -> itemController.create(1L, itemDto));
    }

    @Test
    void updateTest() {
        userController.create(user);
        itemController.create(1L, itemDto);
        ItemDto item = itemDto.toBuilder().name("new name").description("updateDescription").available(false).build();
        itemController.update(item, 1L, 1L);
        assertEquals(item.getDescription(), itemController.getById(1L, 1L).getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto, 1L, 1L));
    }

    @Test
    void updateByWrongUserTest() {
        userController.create(user);
        itemController.create(1L, itemDto);
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto.toBuilder()
                .name("new name").build(), 1L, 10L));
    }

    @Test
    void deleteTest() {
        userController.create(user);
        itemController.create(1L, itemDto);
        assertEquals(1, itemController.getAll(1L, 0, 10).size());
        itemController.delete(1L);
        assertEquals(0, itemController.getAll(1L, 0, 10).size());
    }

    @Test
    void searchTest() {
        userController.create(user);
        itemController.create(1L, itemDto);
        assertEquals(1, itemController.search("Desc", 0, 10).size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.create(user);
        itemController.create(1L, itemDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.search("", 0, 10));
    }

    @Test
    void searchWithWrongFrom() {
        assertThrows(BadRequestException.class, () -> itemController.search("t", -1, 10));
    }

    @Test
    void createCommentTest() {
        userController.create(user);
        ItemDto item = itemController.create(1L, itemDto);
        UserDto userCreate1 = userController.create(user1);
        bookingController.create(BookingItemAndUserId.builder()
                .start(LocalDateTime.of(2022, 10, 20, 12, 15))
                .end(LocalDateTime.of(2022, 10, 27, 12, 15))
                .itemId(item.getId()).build(), userCreate1.getId());
        bookingController.approve(1L, 1L, true);
        itemController.createComment(item.getId(), userCreate1.getId(), comment);
        assertEquals(1, itemController.getById(1L, 1L).getComments().size());
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.createComment(1L, 1L, comment));
    }

    @Test
    void createCommentToWrongItem() {
        userController.create(user);
        assertThrows(NotFoundException.class, () -> itemController.createComment(1L, 1L, comment));
        itemController.create(1L, itemDto);
        assertThrows(BadRequestException.class, () -> itemController.createComment(1L, 1L, comment));
    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(BadRequestException.class, () -> itemController.getAll(1L, -1, 10));
    }
}

