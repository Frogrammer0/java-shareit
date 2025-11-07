

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserShortDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toUserDto_WhenValidUser_ShouldMapCorrectly() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        UserDto result = userMapper.toUserDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void toUser_WhenValidUserDto_ShouldMapCorrectly() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        User result = userMapper.toUser(userDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
        assertEquals(0, result.getId());
    }

    @Test
    void toUserShortDto_WhenValidUser_ShouldMapCorrectly() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        UserShortDto result = userMapper.toUserShortDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());

    }

    @Test
    void toUser_WhenUserDtoWithNullFields_ShouldHandleGracefully() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();

        User result = userMapper.toUser(userDto);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toUserDto_WhenUserWithNullFields_ShouldHandleGracefully() {
        User user = User.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();

        UserDto result = userMapper.toUserDto(user);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getEmail());
    }
}