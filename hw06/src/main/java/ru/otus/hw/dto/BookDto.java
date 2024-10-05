package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookDto {
    private long id;

    private String title;

    private AuthorDto authorDto;

    private GenreDto genreDto;
}
