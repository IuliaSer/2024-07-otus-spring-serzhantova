package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.Constants.TEST_DATA_UTILS_PATH;
import static ru.otus.hw.utils.TestDataUtils.getBookDtos;
import static ru.otus.hw.utils.TestDataUtils.getCommentDtos;

@DataMongoTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CommentServiceImpl.class, CommentMapper.class, BookMapper.class, AuthorMapper.class, GenreMapper.class})
public class CommentServiceImplTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private CommentServiceImpl service;

    private List<BookDto> bookDtos;

    private List<CommentDto> commentDtos;

    @BeforeEach
    void setUp() {
        bookDtos = getBookDtos();
        commentDtos = getCommentDtos();
    }

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource(TEST_DATA_UTILS_PATH + "#getCommentDtos")
    public void findByIdTest(CommentDto expectedComment) {
        var actualComment = service.findById(expectedComment.getId());

        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список всех комментариев")
    @Test
    void findAllByBookIdTest() {
        var expectedComments = List.of(commentDtos.get(0));

        var actualComments = service.findAllByBookId("1");

        assertThat(actualComments).containsExactlyElementsOf(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void insertTest() {
        var expectedComment = new CommentDto(null, "Message_4", bookDtos.get(0));

        var returnedComment = service.insert("Message_4", "1");

        assertThat(returnedComment).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(service.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен выкидывать EntityNotFoundException при не найденной книге")
    @Test
    void insertTest_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.insert("Message_4", "4");})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененный коммент")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateTest() {
        var expectedComment = new CommentDto("1", "Message_4", bookDtos.get(2));

        assertThat(service.findById(expectedComment.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = service.update("1", "Message_4", bookDtos.get(2).getId());

        assertThat(returnedComment).isNotNull()
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
        assertThat(service.findById("1")).isPresent();
        service.deleteById("1");
        assertThat(service.findById("1")).isEmpty();
    }
}