
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("Test User " + id)
                .email("test" + id + "@email.com")
                .build();
    }

    private UserDto createTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();
    }

    private UserDto createTestUserDto(Long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    @Test
    void getAllUsers_WhenValidParameters_ShouldReturnUsers() {
        List<User> users = List.of(
                createTestUser(1L),
                createTestUser(2L)
        );

        List<UserDto> userDtos = List.of(
                createTestUserDto(1L, "User 1", "user1@email.com"),
                createTestUserDto(2L, "User 2", "user2@email.com")
        );

        when(userRepository.findAll(0, 10)).thenReturn(users);
        when(userMapper.toUserDto(users.get(0))).thenReturn(userDtos.get(0));
        when(userMapper.toUserDto(users.get(1))).thenReturn(userDtos.get(1));

        List<UserDto> result = userService.getAllUsers(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDtos, result);
        verify(userRepository).findAll(0, 10);
        verify(userMapper, times(2)).toUserDto(any(User.class));
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        when(userRepository.findAll(0, 10)).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll(0, 10);
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void getAllUsers_WithDifferentPagination_ShouldPassCorrectParameters() {
        List<User> users = List.of(createTestUser(1L));
        List<UserDto> userDtos = List.of(createTestUserDto());

        when(userRepository.findAll(5, 20)).thenReturn(users);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDtos.get(0));

        List<UserDto> result = userService.getAllUsers(5, 20);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll(5, 20);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User user = createTestUser(userId);
        UserDto expectedDto = createTestUserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowNotFoundException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertTrue(exception.getMessage().contains("пользователь с введенным id"));
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void create_WhenValidUser_ShouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("new@email.com")
                .build();

        User user = User.builder()
                .name("New User")
                .email("new@email.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("New User")
                .email("new@email.com")
                .build();

        UserDto expectedDto = createTestUserDto();

        doNothing().when(userValidator).validate(userDto);
        doNothing().when(userValidator).isMailExists(userDto.getEmail());
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(expectedDto);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userValidator).validate(userDto);
        verify(userValidator).isMailExists(userDto.getEmail());
        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(savedUser);
    }

    @Test
    void create_WhenInvalidUser_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("")
                .email("invalid-email")
                .build();

        doThrow(new ValidationException("Invalid data"))
                .when(userValidator).validate(invalidUserDto);


        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.create(invalidUserDto));

        assertNotNull(exception);
        verify(userValidator).validate(invalidUserDto);
        verify(userValidator, never()).isMailExists(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WhenDuplicateEmail_ShouldThrowDuplicatedDataException() {
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("existing@email.com")
                .build();

        doNothing().when(userValidator).validate(userDto);
        doThrow(new DuplicatedDataException("Email already exists"))
                .when(userValidator).isMailExists(userDto.getEmail());

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.create(userDto));

        assertNotNull(exception);
        verify(userValidator).validate(userDto);
        verify(userValidator).isMailExists(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void edit_WhenValidUpdate_ShouldUpdateUser() {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        User existingUser = createTestUser(userId);
        User updatedUser = User.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        UserDto expectedDto = createTestUserDto();

        doNothing().when(userValidator).isUserExists(userId);
        doNothing().when(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);


        UserDto result = userService.edit(userId, updateDto);


        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userValidator).isUserExists(userId);
        verify(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(updatedUser);
    }

    @Test
    void edit_WhenPartialNameUpdate_ShouldUpdateOnlyName() {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .name("Only Name Updated")
                .build();

        User existingUser = createTestUser(userId);
        User updatedUser = User.builder()
                .id(userId)
                .name("Only Name Updated")
                .email(existingUser.getEmail())
                .build();

        UserDto expectedDto = createTestUserDto();

        doNothing().when(userValidator).isUserExists(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);

        UserDto result = userService.edit(userId, updateDto);

        assertNotNull(result);
        verify(userValidator).isUserExists(userId);
        verify(userValidator, never()).validateEmailForOwner(anyString(), anyLong());
        verify(userRepository).save(argThat(user ->
                user.getName().equals("Only Name Updated") &&
                        user.getEmail().equals(existingUser.getEmail())
        ));
    }

    @Test
    void edit_WhenPartialEmailUpdate_ShouldUpdateOnlyEmail() {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .email("only.email@updated.com")
                .build();

        User existingUser = createTestUser(userId);
        User updatedUser = User.builder()
                .id(userId)
                .name(existingUser.getName())
                .email("only.email@updated.com")
                .build();

        UserDto expectedDto = createTestUserDto();

        doNothing().when(userValidator).isUserExists(userId);
        doNothing().when(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);

        UserDto result = userService.edit(userId, updateDto);

        assertNotNull(result);
        verify(userValidator).isUserExists(userId);
        verify(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);
        verify(userRepository).save(argThat(user ->
                user.getName().equals(existingUser.getName()) &&
                        user.getEmail().equals("only.email@updated.com")
        ));
    }

    @Test
    void edit_WhenUserNotExists_ShouldThrowNotFoundException() {
        Long userId = 999L;
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .build();

        doThrow(new NotFoundException("User not found"))
                .when(userValidator).isUserExists(userId);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.edit(userId, updateDto));

        assertNotNull(exception);
        verify(userValidator).isUserExists(userId);
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void edit_WhenDuplicateEmail_ShouldThrowDuplicatedDataException() {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .email("existing@email.com")
                .build();

        doNothing().when(userValidator).isUserExists(userId);
        doThrow(new DuplicatedDataException("Email already used"))
                .when(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.edit(userId, updateDto));

        assertNotNull(exception);
        verify(userValidator).isUserExists(userId);
        verify(userValidator).validateEmailForOwner(updateDto.getEmail(), userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_WhenValidUser_ShouldDeleteUser() {
        Long userId = 1L;

        doNothing().when(userValidator).isUserExists(userId);
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userValidator).isUserExists(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_WhenUserNotExists_ShouldThrowNotFoundException() {
        Long userId = 999L;

        doThrow(new NotFoundException("User not found"))
                .when(userValidator).isUserExists(userId);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.delete(userId));

        assertNotNull(exception);
        verify(userValidator).isUserExists(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getUserOrThrow_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User expectedUser = createTestUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        when(userMapper.toUserDto(expectedUser)).thenReturn(createTestUserDto());
        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
    }
}