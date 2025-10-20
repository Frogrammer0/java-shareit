package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {

    long id;
    String email;
    String name;

}
