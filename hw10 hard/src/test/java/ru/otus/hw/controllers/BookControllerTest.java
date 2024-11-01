//package ru.otus.hw.controllers;
//
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.otus.hw.services.AuthorService;
//import ru.otus.hw.services.BookService;
//import ru.otus.hw.services.GenreService;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//import static ru.otus.hw.utils.TestDataUtils.getAuthorDtos;
//import static ru.otus.hw.utils.TestDataUtils.getBookDtos;
//import static ru.otus.hw.utils.TestDataUtils.getGenreDtos;
//
//@WebMvcTest(BookController.class)
//public class BookControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private BookService bookService;
//
//    @MockBean
//    private AuthorService authorService;
//
//    @MockBean
//    private GenreService genreService;
//
//    @Test
//    @SneakyThrows
//    public void getAllTest() {
//        when(bookService.findAll()).thenReturn(getBookDtos());
//
//        mvc.perform(get("/books/"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("books"))
//                .andExpect(content().string(containsString(getBookDtos().get(0).getTitle())))
//                .andExpect(content().string(containsString(getBookDtos().get(1).getTitle())))
//                .andExpect(content().string(containsString(getBookDtos().get(2).getTitle())))
//                .andExpect(view().name("books"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void getByIdTest() {
//        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));
//
//        mvc.perform(get("/books/1"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("book"))
//                .andExpect(view().name("book"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void updateTest() {
//        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));
//        when(authorService.findAll()).thenReturn(getAuthorDtos());
//        when(genreService.findAll()).thenReturn(getGenreDtos());
//
//        mvc.perform(get("/books/update/1"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("book"))
//                .andExpect(model().attributeExists("authors"))
//                .andExpect(model().attributeExists("genres"))
//                .andExpect(content().string(containsString(getBookDtos().get(0).getTitle())))
//                .andExpect(content().string(containsString(getAuthorDtos().get(0).getFullName())))
//                .andExpect(content().string(containsString(getAuthorDtos().get(1).getFullName())))
//                .andExpect(content().string(containsString(getAuthorDtos().get(2).getFullName())))
//                .andExpect(content().string(containsString(getGenreDtos().get(0).getName())))
//                .andExpect(content().string(containsString(getGenreDtos().get(1).getName())))
//                .andExpect(content().string(containsString(getGenreDtos().get(2).getName())))
//                .andExpect(view().name("update"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void saveTest() {
//        mvc.perform(post("/books/update")
//                        .param("id", "1")
//                        .param("title", "test_title")
//                        .param("authorId", "1")
//                        .param("genreId", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/books/"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void saveTest_ConstrainValidation() {
//        mvc.perform(post("/books/update")
//                        .param("id", "1")
//                        .param("title", "")
//                        .param("authorId", "1")
//                        .param("genreId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("update"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void createTest() {
//        mvc.perform(get("/books/create"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("book"))
//                .andExpect(model().attributeExists("authors"))
//                .andExpect(model().attributeExists("genres"))
//                .andExpect(view().name("create"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void createPostTest() {
//        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));
//
//        mvc.perform(post("/books/create")
//                .param("id", "1")
//                .param("title", "")
//                .param("authorId", "1")
//                .param("genreId", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/books/"));
//    }
//
//    @Test
//    @SneakyThrows
//    public void deleteTest() {
//        mvc.perform(post("/books/delete")
//                .param("id", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/books/"));
//    }
//}
