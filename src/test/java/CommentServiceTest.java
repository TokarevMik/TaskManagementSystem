import org.example.model.Comment;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void testAddComment() {
        Comment comment = new Comment(UUID.randomUUID(), "Test Comment", null, null);
        when(commentRepository.save(comment)).thenReturn(comment);
        Comment savedComment = commentService.addComment(comment);
        assertEquals("Test Comment", savedComment.getText());
    }
}