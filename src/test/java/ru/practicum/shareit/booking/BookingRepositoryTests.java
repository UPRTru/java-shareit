package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.WAITING;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTests {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User user;
    private Item item;
    private User user2;
    private Booking booking;

    @BeforeEach
    void init() {
        user = User.builder()
                .name("name")
                .email("email@email.com")
                .build();
        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        user2 = User.builder()
                .name("name2")
                .email("email2@email.com")
                .build();
        booking = Booking.builder()
                .start(LocalDateTime.of(2023, 10, 10, 10, 10))
                .end(LocalDateTime.of(2023, 11, 10, 10, 10))
                .item(item)
                .booker(user2)
                .status(WAITING)
                .build();
    }

    @Test
    void findAllByItemIdOrderByStartAscTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId()).size(), equalTo(1));
    }

    @Test
    void findAllByBookerTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBooker(user2, Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user2.getId(),
                        item.getId(), APPROVED, LocalDateTime.of(2023, 12, 10, 10, 10)).size(),
                equalTo(1));
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user2,
                LocalDateTime.of(2023, 11, 1, 10, 10), LocalDateTime.now(),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndEndBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndEndBefore(user,
                LocalDateTime.of(2023, 12, 10, 10, 10),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStartAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndStartAfter(user,
                LocalDateTime.now(), Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemIdOrderByStartDesc() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemIdOrderByStartDesc(item.getId()).size(), equalTo(1));
    }

    @Test
    void findAllByBookerAndEndBefore() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerAndEndBefore(user2,
                LocalDateTime.of(2023, 11, 11, 10, 10),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByBookerAndStartAfter() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerAndStartAfter(user2,
                LocalDateTime.of(2023, 10, 1, 10, 10),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));

    }

    @Test
    void findAllByBookerAndStatusEquals() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerAndStatusEquals(user2, WAITING, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwner() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwner(user, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStartBeforeAndEndAfter() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.of(2023, 10, 11, 10, 10),
                        LocalDateTime.of(2023, 11, 9, 10, 10),
                        Pageable.ofSize(10))
                .stream().count(), equalTo(1L));

    }

}

