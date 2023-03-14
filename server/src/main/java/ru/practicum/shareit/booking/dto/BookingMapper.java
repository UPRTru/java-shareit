package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .item(booking.getItem())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .item(bookingDto.getItem())
                .build();
    }

    public static Booking toBooking(BookingItemAndUserId bookingItemAndUserId) {
        return Booking.builder()
                .id(bookingItemAndUserId.getId())
                .start(bookingItemAndUserId.getStart())
                .end(bookingItemAndUserId.getEnd())
                .build();
    }

    public static BookingItemAndUserId toBookingItemAndUserId(Booking booking) {
        return BookingItemAndUserId.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}