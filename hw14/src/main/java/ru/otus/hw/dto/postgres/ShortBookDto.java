package ru.otus.hw.dto.postgres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ShortBookDto {
    private Long id;

    private String title;

    private Long authorId;

    private Long genreId;
}
