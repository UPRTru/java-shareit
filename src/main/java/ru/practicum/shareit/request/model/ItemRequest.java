package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    private Long id;
    @NotBlank
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
