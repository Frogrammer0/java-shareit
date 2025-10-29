package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "requests", schema = "public")
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
