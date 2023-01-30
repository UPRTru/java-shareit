package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingItemAndUserId;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> result = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        result.forEach(itemDto -> {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).isEmpty() ? null
                    : toBookingItemAndUserId(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ? null
                    : toBookingItemAndUserId(bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId()).get(0)));
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        });
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item id: " + id + " не найден."));
        ItemDto result = toItemDto(item);
        result.setComments(commentRepository.findAllByItemId(id)
                .stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (item.getOwner().getId().equals(ownerId)) {
            result.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(id).isEmpty() ? null :
                    toBookingItemAndUserId(bookingRepository.findAllByItemIdOrderByStartAsc(id).get(0)));
            result.setNextBooking(result.getLastBooking() == null ?
                    null : toBookingItemAndUserId(bookingRepository.findAllByItemIdOrderByStartDesc(result.getId())
                    .get(0)));
        }
        return result;
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь id: " + userId + " не найден."));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id: " + userId + " не найден."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id: " + itemId + " не найден."));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId, itemId, APPROVED, LocalDateTime.now()
                ).isEmpty()) {
            throw new BadRequestException("Item не брался в аренду или аренда не закончилась.");
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return toCommentDto(comment);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Item id: " + id + " не найден."));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У пользователя id: " + userId + " нет item id: " + id);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> searchedItems = new ArrayList<>();
        if (text.isBlank()) {
            return searchedItems;
        }
        for (Item item : itemRepository.findAll()) {
            if (isSearched(text, item)) {
                searchedItems.add(toItemDto(item));
            }
        }
        return searchedItems;
    }

    private Boolean isSearched(String text, Item item) {
        return item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable();
    }
}
