package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingItemAndUserId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;

    private BookingItemAndUserId lastBooking;

    private BookingItemAndUserId nextBooking;

    private List<CommentDto> comments;
}
