package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(long id);

    User create(User user);

    User edit(long userId, User newUser);

    void delete(Long id);

    public void isUserExist(long userId);

}
