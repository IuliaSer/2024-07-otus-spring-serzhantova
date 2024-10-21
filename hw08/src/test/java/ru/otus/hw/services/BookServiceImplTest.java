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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.Constants.TEST_DATA_UTILS_PATH;
import static ru.otus.hw.utils.TestDataUtils.*;

@DataMongoTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({BookServiceImpl.class, BookMapper.class, AuthorMapper.class, GenreMapper.class, CommentServiceImpl.class, CommentMapper.class})
public class BookServiceImplTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private CommentServiceImpl commentService;

    private List<AuthorDto> authorDtos;

    private List<GenreDto> genreDtos;

    private List<BookDto> bookDtos;

    @BeforeEach
    void setUp() {
        authorDtos = getAuthorDtos();
        genreDtos = getGenreDtos();
        bookDtos = getBookDtos(authorDtos, genreDtos);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource(TEST_DATA_UTILS_PATH + "#getBookDtos")
    public void findByIdTest(BookDto expectedBook) {
        var actualBook = bookService.findById(expectedBook.getId());

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void findAllTest() {
        var actualBooks = bookService.findAll();
        var expectedBooks = bookDtos;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void insertTest() {
        var expectedBook = new BookDto(null, "BookTitle_10500", authorDtos.get(0), genreDtos.get(0));
        var returnedBook = bookService.insert("BookTitle_10500", authorDtos.get(0).getId(), genreDtos.get(0).getId());

        assertThat(returnedBook).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен не сохранять новую книгу")
    @Test
    void insertTest_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {bookService.insert("BookTitle_10500", "5", genreDtos.get(0).getId());})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененную книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateTest() {
        var expectedBook = new BookDto("1", "BookTitle_10500", authorDtos.get(2), genreDtos.get(2));

        assertThat(bookService.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookService.update("1", "BookTitle_10500", authorDtos.get(2).getId(), genreDtos.get(2).getId());
        assertThat(returnedBook).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);

        for (CommentDto commentDto : commentService.findAllByBookId("1")) {
            assertThat(commentDto.getBookDto()).isEqualTo(returnedBook);
        }
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void updateTest_NoAuthorFound_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {bookService.update("1","BookTitle_10500", "4", "1");})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void updateTest_NoGenreFound_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {bookService.update("1","BookTitle_10500", "1", "4");})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен удалять книгу по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById("1")).isPresent();

        bookService.deleteById("1");

        assertThat(bookService.findById("1")).isEmpty();
    }

    @Test
    @DisplayName("должен удалять комментарии, содержащий удаленную книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteCommentsAfterDeleteBook() {
        assertThat(!commentService.findAllByBookId("1").isEmpty());

        bookService.deleteById("1");

        assertThat(commentService.findAllByBookId("1").isEmpty());
    }
}