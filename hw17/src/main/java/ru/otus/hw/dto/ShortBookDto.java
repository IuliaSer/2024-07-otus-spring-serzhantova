package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortBookDto {
    private long id;

    @NotBlank(message = "Title should not be empty")
    @Size(min = 2, max = 20, message = "Title should have expected size")
    private String title;

    @NotNull(message = "Author should not be null")
    private Long authorId;

    @NotNull(message = "Genre should not be null")
    private Long genreId;
}
