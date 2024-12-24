package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ShortBookDto {
    private String id;

    private String title;

    private String authorId;

    private String genreId;
}
