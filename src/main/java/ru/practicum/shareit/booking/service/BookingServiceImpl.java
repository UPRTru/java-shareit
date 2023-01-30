package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemAndUserId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.*;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDto create(BookingItemAndUserId bookingItemAndUserId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id: " + userId + " не найден."));
        Item item = itemRepository.findById(bookingItemAndUserId.getItemId())
                .orElseThrow(() -> new NotFoundException("Item id: " + bookingItemAndUserId.getItemId() + " не найден."));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item id: " + bookingItemAndUserId.getItemId() + " пренадлежит пользователю.");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item id: " + bookingItemAndUserId.getItemId() + " недоступен.");
        }
        if (bookingItemAndUserId.getEnd().isBefore(bookingItemAndUserId.getStart())) {
            throw new BadRequestException("Проверьте дату.");
        }
        Booking result = Booking.builder()
                .start(bookingItemAndUserId.getStart())
                .end(bookingItemAndUserId.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();
        bookingRepository.save(result);
        return toBookingDto(result);
    }

    @Transactional
    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование id: " + bookingId + " не найдено."));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("У пользователя id: " + userId + "Бронирование id: " + bookingId + " не найдено.");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Ошибка бронирования.");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id: " + userId + " не найден."));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwner(user, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, REJECTED,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id: " + userId + " не найден."));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository.findAllByBooker(user,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, WAITING,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, REJECTED,
                        Sort.by(Sort.Direction.DESC, "start")));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование id: " + bookingId + " не найдено."));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("У пользователя id: " + userId + "Бронирование id: " + bookingId + " не найдено.");
        }
        return toBookingDto(booking);
    }
}
