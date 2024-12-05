package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.hw.utils.Constants.TEST_DATA_UTILS_PATH;
import static ru.otus.hw.utils.TestDataUtils.getAuthorDtos;
import static ru.otus.hw.utils.TestDataUtils.getBookDtos;
import static ru.otus.hw.utils.TestDataUtils.getGenreDtos;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({BookServiceImpl.class, BookMapper.class, AuthorMapper.class, GenreMapper.class
        })
public class BookServiceImplTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookServiceImpl service;

    @MockBean
    private AclServiceWrapperService aclServiceWrapperService;

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
        var actualBook = service.findById(expectedBook.getId());

        assertThat(actualBook).isNotNull()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void findAllTest() {
        var actualBooks = service.findAll();
        var expectedBooks = bookDtos;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void insertTest() {
        var expectedBook = new BookDto(4, "BookTitle_10500", authorDtos.get(0), genreDtos.get(0));
        var returnedBook = service.insert("BookTitle_10500", authorDtos.get(0).getId(), genreDtos.get(0).getId());

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(service.findById(returnedBook.getId()))
                .isNotNull()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен не сохранять новую книгу")
    @Test
    void insertTest_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.insert("BookTitle_10500", 5, genreDtos.get(0).getId());})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененную книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateTest() {
        var expectedBook = new BookDto(1L, "BookTitle_10500", authorDtos.get(2), genreDtos.get(2));

        assertThat(service.findById(expectedBook.getId()))
                .isNotNull()
                .isNotEqualTo(expectedBook);

        var returnedBook = service.update(1, "BookTitle_10500", authorDtos.get(2).getId(), genreDtos.get(2).getId());
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(service.findById(returnedBook.getId()))
                .isNotNull()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен выкидывать EntityNotFoundException при не найденом авторе")
    @Test
    void updateTest_NoAuthorFound_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.update(1,"BookTitle_10500", 4, 1);})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void updateTest_NoGenreFound_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.update(1,"BookTitle_10500", 1, 4);})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен удалять книгу по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldDeleteBook() {
        assertThat(service.findById(1L)).isNotNull();
        service.deleteById(1L);
        assertThrows(EntityNotFoundException.class, () -> service.findById(1L));
    }
}