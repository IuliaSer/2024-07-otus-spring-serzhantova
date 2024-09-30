package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.Constants.TEST_DATA_UTILS_PATH;
import static ru.otus.hw.utils.TestDataUtils.getDbBooks;
import static ru.otus.hw.utils.TestDataUtils.getDbComments;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({JpaCommentRepository.class, CommentServiceImpl.class, JpaBookRepository.class})
public class CommentServiceImplTest {

    @Autowired
    private JpaCommentRepository repository;

    @Autowired
    private CommentServiceImpl service;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbBooks = getDbBooks();
        dbComments = getDbComments();
    }

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource(TEST_DATA_UTILS_PATH + "#getDbComments")
    public void findByIdTest(Comment expectedComment) {
        var actualComment = service.findById(expectedComment.getId());

        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список всех комментариев")
    @Test
    void findAllByBookIdTest() {
        var expectedComments = List.of(dbComments.get(0));

        var actualComments = service.findAllByBookId(1);

        assertThat(actualComments).containsExactlyElementsOf(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void insertTest() {
        var expectedComment = new Comment(4, "Message_4", dbBooks.get(0));

        var returnedComment = service.insert("Message_4", 1);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(service.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void insertTest_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.insert("Message_4", 4);})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененный коммент")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateTest() {
        var expectedComment = new Comment(1L, "Message_4", dbBooks.get(2));

        assertThat(service.findById(expectedComment.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = service.update(1, "Message_4", dbBooks.get(2).getId());

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(service.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldDeleteComment() {
        assertThat(service.findById(1L)).isPresent();
        service.deleteById(1L);
        assertThat(service.findById(1L)).isEmpty();
    }
}
