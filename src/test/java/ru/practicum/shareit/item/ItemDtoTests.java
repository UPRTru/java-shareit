package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@JsonTest
public class ItemDtoTests {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void testItemDtoJson() throws Exception {
        ItemDto itemDto = ItemDto
                .builder()
                .id(1L)
                .name("item")
                .available(true)
                .description("descriptionOfItem")
                .build();
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("descriptionOfItem");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }

    @Test
    void testItemMapper() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .build();
        ItemDto itemDto = toItemDto(item);
        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getRequest().getId(), equalTo(itemDto.getRequestId()));
        Item item1 = toItem(itemDto);
        assertThat(item1.getId(), equalTo(itemDto.getId()));
        assertThat(item1.getName(), equalTo(itemDto.getName()));
        assertThat(item1.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item1.getAvailable(), equalTo(itemDto.getAvailable()));
        Item item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .build();
        ItemDto itemDto1 = toItemDto(item2);
        assertThat(item2.getId(), equalTo(itemDto1.getId()));
        assertThat(item2.getName(), equalTo(itemDto1.getName()));
        assertThat(item2.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(item2.getAvailable(), equalTo(itemDto1.getAvailable()));
        assertThat(item2.getRequest(), equalTo(itemDto1.getRequestId()));
    }

    @Test
    void testCommentMapper() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .author(user)
                .created(LocalDateTime.now())
                .build();
        CommentDto commentDto = toCommentDto(comment);
        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getCreated(), equalTo(commentDto.getCreated()));
        Comment comment1 = toComment(commentDto);
        assertThat(comment1.getId(), equalTo(commentDto.getId()));
        assertThat(comment1.getText(), equalTo(commentDto.getText()));
        assertThat(comment1.getCreated(), equalTo(commentDto.getCreated()));
    }
}
