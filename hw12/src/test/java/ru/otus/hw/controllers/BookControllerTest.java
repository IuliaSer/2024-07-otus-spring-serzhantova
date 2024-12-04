package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.configuration.SecurityConfig;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.utils.TestDataUtils.getBookDtos;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void getAllTest() {
        when(bookService.findAll()).thenReturn(getBookDtos());

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos())));
    }

    @Test
    @SneakyThrows
    public void getAllTest_UserNotAuthenticated() {
        when(bookService.findAll()).thenReturn(getBookDtos());

        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void getByIdTest() {
        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));

        mvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos().get(0))));
    }

    @Test
    @SneakyThrows
    public void getByIdTest_UserNotAuthenticated() {
        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));

        mvc.perform(get("/books/1"))
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void getByIdTest_EntityNotFound() {
        when(bookService.findById(10)).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/books/10"))
                .andExpect(status().is4xxClientError());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_ADMIN"})
    @Test
    @SneakyThrows
    public void updateTest() {
        BookDto expectedBookDto = getBookDtos().get(0);
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.update(1, "BookTitle_1", 1, 1)).thenReturn(expectedBookDto);

        mvc.perform(put("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookDto)));
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void updateTest_NotAuthorized() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.update(1, "BookTitle_1", 1, 1)).thenReturn(getBookDtos().get(0));

        mvc.perform(put("/books")
                    .contentType("application/json")
                    .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_ADMIN"})
    @Test
    @SneakyThrows
    public void updateTest_InvalidBook_TitleIsEmpty() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "", 1L, 1L);
        when(bookService.update(1, "", 1, 1)).thenReturn(getBookDtos().get(0));

        mvc.perform(put("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_ADMIN"})
    @Test
    @SneakyThrows
    public void createTest() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.insert("BookTitle_1", 1, 1)).thenReturn(getBookDtos().get(0));

        mvc.perform(post("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos().get(0))));
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void createTest_NotAuthorized() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.insert("BookTitle_1", 1, 1)).thenReturn(getBookDtos().get(0));

        mvc.perform(post("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_ADMIN"})
    @Test
    @SneakyThrows
    public void createTest_InvalidBook_NoAuthor() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", null, 1L);
        when(bookService.insert("BookTitle_1", 1, 1)).thenReturn(getBookDtos().get(0));

        mvc.perform(post("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_ADMIN"})
    @Test
    @SneakyThrows
    public void deleteTest() {
        mvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "User_1", authorities = {"ROLE_USER"})
    @Test
    @SneakyThrows
    public void deleteTest_NotAuthorized() {
        mvc.perform(delete("/books/1"))
                .andExpect(status().isForbidden());
    }
}
