package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    private final BookConverter bookConverter;

    public String commentDtoToString(CommentDto comment) {
        return "Id: %s, message: %s, book: [%s]".formatted(
                comment.getId(),
                comment.getMessage(),
                bookConverter.bookDtoToString(comment.getBookDto()));
    }
}