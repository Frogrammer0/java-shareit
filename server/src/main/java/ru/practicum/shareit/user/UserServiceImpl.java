package ru.practicum.shareit.user;

import exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import user.dto.UserDto;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    UserValidator userValidator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("получение всех пользователей в UserService");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        log.info("получение пользователя в UserService id = {}", id);
        return userMapper.toUserDto(getUserOrThrow(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("создание пользователя в UserService");
        userValidator.validate(userDto);
        userValidator.isMailExists(userDto.getEmail());
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }


    @Override
    public UserDto edit(long userId, UserDto userDto) {
        log.info("редактирование пользователя в UserService");
        userValidator.isUserExists(userId);
        userValidator.isMailExists(userDto.getEmail());
        User user = userMapper.toUser(userDto);
        user.setId(userId);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        log.info("удаление пользователя в UserService");
        userValidator.isUserExists(id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("пользователь с введенным id = " + id + " не найдена")
        );
    }

}
