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
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.Constants.TEST_DATA_UTILS_PATH;
import static ru.otus.hw.utils.TestDataUtils.getDbAuthors;
import static ru.otus.hw.utils.TestDataUtils.getDbBooks;
import static ru.otus.hw.utils.TestDataUtils.getDbGenres;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({JpaBookRepository.class, BookServiceImpl.class, JpaAuthorRepository.class, JpaGenreRepository.class  })
public class BookServiceImplTest {

    @Autowired
    private JpaBookRepository repository;

    @Autowired
    private BookServiceImpl service;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource(TEST_DATA_UTILS_PATH + "#getDbBooks")
    public void findByIdTest(Book expectedBook) {
        var actualBook = service.findById(expectedBook.getId());

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void findAllTest() {
        var actualBooks = service.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void insertTest() {
        var expectedBook = new Book(4, "BookTitle_10500", dbAuthors.get(0), dbGenres.get(0));
        var returnedBook = service.insert("BookTitle_10500", dbAuthors.get(0).getId(), dbGenres.get(0).getId());

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(service.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен не сохранять новую книгу")
    @Test
    void insertTest_EntityNotFoundException() {
        assertThatThrownBy( () ->
        {service.insert("BookTitle_10500", 5, dbGenres.get(0).getId());})
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен сохранять измененную книгу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateTest() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2), dbGenres.get(2));

        assertThat(service.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = service.update(1, "BookTitle_10500", dbAuthors.get(2).getId(), dbGenres.get(2).getId());
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(service.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
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
        assertThat(service.findById(1L)).isPresent();
        service.deleteById(1L);
        assertThat(service.findById(1L)).isEmpty();
    }
}
