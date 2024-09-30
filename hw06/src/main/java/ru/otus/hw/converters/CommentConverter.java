package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.entity.Comment;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    private final BookConverter bookConverter;

    public String commentToString(Comment comment) {
        return "Id: %d, message: %s, book: [%s]".formatted(
                comment.getId(),
                comment.getMessage(),
                bookConverter.bookToString(comment.getBook()));
    }
}