package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOwnerDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.*;

public class ItemRequestMapperTests {
    @Test
    void testRequestMapper() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequestDto.getItems().size(), equalTo(0));
        ItemRequest itemRequest1 = toItemRequest(itemRequestDto);
        assertThat(itemRequest1.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequest1.getDescription(), equalTo(itemRequestDto.getDescription()));
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        ItemRequestOwnerDto itemRequestOwnerDto = toItemShortDto(item);
        assertThat(itemRequestOwnerDto.getId(), equalTo(item.getId()));
        assertThat(itemRequestOwnerDto.getName(), equalTo(item.getName()));
        assertThat(itemRequestOwnerDto.getOwnerId(), equalTo(item.getOwner().getId()));
        assertThat(itemRequestOwnerDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemRequestOwnerDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemRequestOwnerDto.getRequestId(), equalTo(item.getRequest().getId()));
    }
}
