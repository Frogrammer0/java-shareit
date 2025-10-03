package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Data
public class User {

    long id;
    String email;
    String name;

}
