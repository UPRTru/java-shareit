package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    private String name;
    @Column(unique = true, name = "user_email")
    @Email
    @NotBlank
    private String email;

}

