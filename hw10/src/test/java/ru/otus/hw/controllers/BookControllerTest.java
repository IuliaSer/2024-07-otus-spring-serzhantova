package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.services.BookService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.utils.TestDataUtils.getBookDtos;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

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
    public void getByIdTest() {
        when(bookService.findById(1)).thenReturn(getBookDtos().get(0));

        mvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos().get(0))));
    }

    @Test
    @SneakyThrows
    public void updateTest() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.update(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(getBookDtos().get(0));

        mvc.perform(put("/books")
                    .contentType("application/json")
                    .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos().get(0))));
    }

    @Test
    @SneakyThrows
    public void createTest() {
        ShortBookDto shortBookDto = new ShortBookDto(1, "BookTitle_1", 1L, 1L);
        when(bookService.insert(anyString(), anyLong(), anyLong())).thenReturn(getBookDtos().get(0));

        mvc.perform(post("/books")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString((shortBookDto))))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(getBookDtos().get(0))));
    }

    @Test
    @SneakyThrows
    public void deleteTest() {
        mvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }
}
