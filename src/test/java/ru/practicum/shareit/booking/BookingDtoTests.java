package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemAndUserId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.dto.BookingMapper.*;

@JsonTest
public class BookingDtoTests {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoJson() throws Exception {
        BookingDto bookingDtoJson = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 12, 10, 10, 1))
                .end(LocalDateTime.of(2022, 12, 20, 10, 10, 1))
                .build();
        JsonContent<BookingDto> result = json.write(bookingDtoJson);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2022, 12, 12, 10, 10, 1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2022, 12, 20, 10, 10, 1).toString());
    }

    @Test
    void testMapperBooking() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 12, 10, 10, 1))
                .end(LocalDateTime.of(2022, 12, 20, 10, 10, 1))
                .booker(user)
                .item(item)
                .status(APPROVED)
                .build();
        Booking booking = toBooking(bookingDto);
        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDto.getItem(), equalTo(booking.getItem()));
        assertThat(bookingDto.getBooker(), equalTo(booking.getBooker()));
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));
        BookingDto bookingDtoTest = toBookingDto(booking);
        assertThat(bookingDtoTest.getId(), equalTo(booking.getId()));
        assertThat(bookingDtoTest.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoTest.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoTest.getItem(), equalTo(booking.getItem()));
        assertThat(bookingDtoTest.getBooker(), equalTo(booking.getBooker()));
        assertThat(bookingDtoTest.getStatus(), equalTo(booking.getStatus()));
        BookingItemAndUserId bookingItemAndUserId = toBookingItemAndUserId(booking);
        assertThat(bookingItemAndUserId.getId(), equalTo(booking.getId()));
        assertThat(bookingItemAndUserId.getStart(), equalTo(booking.getStart()));
        assertThat(bookingItemAndUserId.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingItemAndUserId.getItemId(), equalTo(booking.getItem().getId()));
        assertThat(bookingItemAndUserId.getBookerId(), equalTo(booking.getBooker().getId()));
        Booking booking1 = toBooking(bookingItemAndUserId);
        assertThat(bookingItemAndUserId.getId(), equalTo(booking1.getId()));
        assertThat(bookingItemAndUserId.getStart(), equalTo(booking1.getStart()));
        assertThat(bookingItemAndUserId.getEnd(), equalTo(booking1.getEnd()));
    }

}
