package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "item_name", length = 100, nullable = false)
    private String name;
    @NotBlank
    @Column(name = "item_description", length = 600, nullable = false)
    private String description;
    @Column
    @NotNull
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "id_owner", referencedColumnName = "id", nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
