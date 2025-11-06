package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers(
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("getAllUsers in UserController");
        return userService.getAllUsers(from, size);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("getUserById in UserController");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("create in UserController");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto edit(
            @PathVariable long userId,
            @RequestBody UserDto userDto
    ) {
        log.info("edit in UserController");
        return userService.edit(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("delete in UserController");
        userService.delete(userId);
    }


}
