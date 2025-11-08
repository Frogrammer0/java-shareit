package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "requests", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
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
    LocalDateTime created;


}
