package ru.otus.hw.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.otus.hw.utils.TestDataUtils.getBookDtos;
import static ru.otus.hw.utils.TestDataUtils.getDbAuthors;
import static ru.otus.hw.utils.TestDataUtils.getDbBooks;
import static ru.otus.hw.utils.TestDataUtils.getDbGenres;

@SpringBootTest(classes = {BookController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({BookRepository.class, BookMapper.class, AuthorRepository.class, GenreRepository.class})
@EnableAutoConfiguration
public class BookControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookMapper bookMapper;

    @Autowired
    private WebTestClient webClient;

    @Test
    void getAllTest()
    {
        Flux<Book> booksFlux = Flux.fromIterable(getDbBooks());
        when(bookRepository.findAll()).thenReturn(booksFlux);
        when(bookMapper.convertToBookDto(getDbBooks().get(0))).thenReturn(getBookDtos().get(0));
        when(bookMapper.convertToBookDto(getDbBooks().get(1))).thenReturn(getBookDtos().get(1));
        when(bookMapper.convertToBookDto(getDbBooks().get(2))).thenReturn(getBookDtos().get(2));

        webClient.get().uri("/books")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class);
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {
        Mono<Book> booksMono = Mono.just(getDbBooks().get(0));
        BookDto expectedBookDto = getBookDtos().get(0);
        when(bookRepository.findById("1")).thenReturn(booksMono);
        when(bookMapper.convertToBookDto(getDbBooks().get(0))).thenReturn(expectedBookDto);

        WebTestClient.ResponseSpec response = webClient.get().uri("/books/1")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus()
                .isOk();

        checkContent(response, expectedBookDto);
    }

    @Test
    @SneakyThrows
    public void updateTest() {
        ShortBookDto shortBookDto = new ShortBookDto("1", "BookTitle_1", "1", "1");
        setUpForUpdateAndCreateTest();

        WebTestClient.ResponseSpec response = webClient.put().uri("/books/1")
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(BodyInserters.fromValue(shortBookDto))
                .exchange()
                .expectStatus()
                .isOk();

        checkContent(response, getBookDtos().get(0));
    }

    @Test
    @SneakyThrows
    public void createTest() {
        ShortBookDto shortBookDto = new ShortBookDto("1", "BookTitle_1", "1", "1");
        setUpForUpdateAndCreateTest();

        WebTestClient.ResponseSpec response = webClient.post().uri("/books")
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(BodyInserters.fromValue(shortBookDto))
                .exchange()
                .expectStatus()
                .isCreated();

        checkContent(response, getBookDtos().get(0));
    }

    @Test
    @SneakyThrows
    public void deleteTest() {
        webClient.delete().uri("/books/1")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    private void checkContent(WebTestClient.ResponseSpec response, BookDto expectedBookDto) {
        AuthorDto expectedAuthorDto = expectedBookDto.getAuthorDto();
        GenreDto expectedGenreDto = expectedBookDto.getGenreDto();

        response.expectBody()
                .jsonPath("$.title").isEqualTo(expectedBookDto.getTitle())
                .jsonPath("$.authorDto.id").isEqualTo(expectedAuthorDto.getId())
                .jsonPath("$.authorDto.fullName").isEqualTo(expectedAuthorDto.getFullName())
                .jsonPath("$.genreDto.id").isEqualTo(expectedGenreDto.getId())
                .jsonPath("$.genreDto.name").isEqualTo(expectedGenreDto.getName());
    }

    private void setUpForUpdateAndCreateTest() {
        Mono<Book> bookMono = Mono.just(getDbBooks().get(0));

        when(bookRepository.findById("1")).thenReturn(bookMono);
        when(genreRepository.findById("1")).thenReturn(Mono.just(getDbGenres().get(0)));
        when(authorRepository.findById("1")).thenReturn(Mono.just(getDbAuthors().get(0)));
        when(bookRepository.save(any())).thenReturn(bookMono);
        when(bookMapper.convertToBookDto(getDbBooks().get(0))).thenReturn(getBookDtos().get(0));
    }
}
