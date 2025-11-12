
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapper commentMapper;

    @Test
    void toCommentDto_WhenValidComment_ShouldMapCorrectly() {
        User author = User.builder()
                .id(1L)
                .name("Test Author")
                .email("author@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(author)
                .item(item)
                .created(LocalDateTime.of(2023, 12, 25, 10, 0))
                .build();

        CommentDto result = commentMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals("Test Author", result.getAuthorName());
        assertNotNull(result.getItem());
        assertEquals(1L, result.getItem().getId());
        assertEquals("Test Item", result.getItem().getName());
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 0), result.getCreated());
    }

    @Test
    void toComment_WhenValidCommentDto_ShouldMapCorrectly() {
        User author = User.builder()
                .id(1L)
                .name("Test Author")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .build();

        Comment result = commentMapper.toComment(commentDto, item, author);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals(author, result.getAuthor());
        assertEquals(item, result.getItem());
        assertNotNull(result.getCreated());
    }
}