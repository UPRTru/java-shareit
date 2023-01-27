package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : itemStorage.findAll()) {
            if (item.getOwner().getId().equals(userId)) {
                result.add(toItemDto(item));
            }
        }
        return result;
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemStorage.findById(id).orElseThrow(()
                -> new NotFoundException("Item id: " + id + " не найден "));
        return toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь id: " + userId + " не найден"));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemStorage.create(item);
        return toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemStorage.findById(id).orElseThrow(()
                -> new NotFoundException("Item id: " + id + " не найден "));
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
        return toItemDto(itemStorage.update(item));
    }

    @Override
    public void delete(Long id) {
        getById(id);
        itemStorage.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> searchedItems = new ArrayList<>();
        if (text.isBlank()) {
            return searchedItems;
        }
        for (Item item : itemStorage.findAll()) {
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
