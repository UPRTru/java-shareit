package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@JsonTest
public class UserDtoTests {
    @Autowired
    JacksonTester<UserDto> json;

    @Test
    void testUserDtoJson() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.ru");
    }

    @Test
    void testUserMapper() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();
        UserDto userDto = toUserDto(user);
        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        User user1 = toUser(userDto);
        assertThat(user1.getId(), equalTo(userDto.getId()));
        assertThat(user1.getName(), equalTo(userDto.getName()));
        assertThat(user1.getEmail(), equalTo(userDto.getEmail()));
    }
}
