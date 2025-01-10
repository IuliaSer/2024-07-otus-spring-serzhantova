package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.entity.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final BookMapper bookMapper;

    public CommentDto convertToCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .bookDto(bookMapper.convertToBookDto(comment.getBook()))
                .build();
    }
}
