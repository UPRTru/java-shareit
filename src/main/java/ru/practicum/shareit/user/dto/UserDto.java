package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Setter
@Getter
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;

}
