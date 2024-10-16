package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private String id;

    private String message;

    private BookDto bookDto;
}
