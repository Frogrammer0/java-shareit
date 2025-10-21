package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    User requestor;

    @Column
    LocalDate created;
}
