package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserDto {

    long id;

    @Email(message = "Некорректный email")
    String email;


    String name;
}
