package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query(value = """
            select * from user u
            order by u.id desc
            limit :size offset :from
            """, nativeQuery = true)
    List<User> findAll(int from, int size);
}
