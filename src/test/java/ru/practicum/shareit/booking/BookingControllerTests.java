package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemAndUserId;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTests {
    @Autowired
    private BookingController bookingController;
    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
    private ItemDto itemDto;
    private User user;
    private User user1;

    private BookingItemAndUserId bookingItemAndUserId;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        user = User.builder()
                .name("name")
                .email("user@email.com")
                .build();
        user1 = User.builder()
                .name("name")
                .email("user1@email.com")
                .build();
        bookingItemAndUserId = BookingItemAndUserId.builder()
                .start(LocalDateTime.of(2022, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(1L).build();
    }

    @Test
    void createTest() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        BookingDto booking = bookingController.create(bookingItemAndUserId, userCreate1.getId());
        assertEquals(1L, bookingController.getById(booking.getId(), userCreate1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingItemAndUserId, 1L));
    }

    @Test
    void createForWrongItemTest() {
        userController.create(user);
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingItemAndUserId, 1L));
    }

    @Test
    void createByOwnerTest() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingItemAndUserId, 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        User userCreate = userController.create(user);
        itemDto.setAvailable(false);
        itemController.create(userCreate.getId(), itemDto);
        userController.create(user1);
        assertThrows(BadRequestException.class, () -> bookingController.create(bookingItemAndUserId, 2L));
    }

    @Test
    void createWithWrongEndDate() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        bookingItemAndUserId.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(BadRequestException.class, () -> bookingController.create(bookingItemAndUserId, userCreate1.getId()));
    }

    @Test
    void approveTest() {
        User userCreate = userController.create(user);
        ItemDto item = itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        BookingDto booking = bookingController.create(BookingItemAndUserId.builder()
                .start(LocalDateTime.of(2022, 10, 24, 12, 30))
                .end(LocalDateTime.of(2022, 11, 10, 13, 0))
                .itemId(item.getId()).build(), userCreate1.getId());
        assertEquals(WAITING, bookingController.getById(booking.getId(), userCreate1.getId()).getStatus());
        bookingController.approve(booking.getId(), userCreate.getId(), true);
        assertEquals(APPROVED, bookingController.getById(booking.getId(), userCreate1.getId()).getStatus());
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void approveByWrongUserTest() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        bookingController.create(bookingItemAndUserId, userCreate1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, 2L, true));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        bookingController.create(bookingItemAndUserId, userCreate1.getId());
        bookingController.approve(1L, 1L, true);
        assertThrows(BadRequestException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void getAllByUserTest() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        BookingDto booking = bookingController.create(bookingItemAndUserId, userCreate1.getId());
        assertEquals(1, bookingController.getAllByUser(userCreate1.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(userCreate1.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userCreate1.getId(), "PAST", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(userCreate1.getId(), "CURRENT", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userCreate1.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userCreate1.getId(), "REJECTED", 0, 10).size());
        bookingController.approve(booking.getId(), userCreate.getId(), true);
        assertEquals(1, bookingController.getAllByOwner(userCreate.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(userCreate.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(userCreate.getId(), "WAITING", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(userCreate.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(userCreate.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(userCreate.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getAllByUser(1L, "ALL", 0, 10));
        assertThrows(NotFoundException.class, () -> bookingController.getAllByOwner(1L, "ALL", 0, 10));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        User userCreate = userController.create(user);
        itemController.create(userCreate.getId(), itemDto);
        User userCreate1 = userController.create(user1);
        bookingController.create(bookingItemAndUserId, userCreate1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 10L));
    }
}
