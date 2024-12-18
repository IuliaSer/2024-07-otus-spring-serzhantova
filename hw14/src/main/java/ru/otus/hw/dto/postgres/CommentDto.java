package ru.otus.hw.dto.postgres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    private String message;

    private Long bookId;
}
