package ru.otus.hw.migration.processors;


import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.postgres.CommentDto;
import ru.otus.hw.entity.Comment;

public class CommentProcessor implements ItemProcessor<Comment, CommentDto> {

    @Override
    public CommentDto process(Comment item) {
        return new CommentDto(Long.valueOf(item.getId()), item.getMessage(), Long.valueOf(item.getBook().getId()));
    }
}