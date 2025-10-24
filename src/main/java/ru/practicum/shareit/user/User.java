package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "users", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    String email;

    @Column
    String name;

}
