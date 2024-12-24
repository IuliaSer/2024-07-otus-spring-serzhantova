package ru.otus.hw.migration.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.ShortCommentDto;
import ru.otus.hw.entity.Comment;

public class CommentProcessor implements ItemProcessor<Comment, ShortCommentDto> {

    @Override
    public ShortCommentDto process(Comment item) {
        return new ShortCommentDto(item.getId(), item.getMessage(), item.getBook().getId());
    }
}