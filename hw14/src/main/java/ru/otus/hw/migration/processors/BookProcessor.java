package ru.otus.hw.migration.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.postgres.ShortBookDto;
import ru.otus.hw.entity.Book;

public class BookProcessor implements ItemProcessor<Book, ShortBookDto> {

    @Override
    public ShortBookDto process(Book item) {
        return new ShortBookDto(Long.valueOf(item.getId()), item.getTitle(), Long.valueOf(item.getAuthor().getId()),
                Long.valueOf(item.getGenre().getId()));
    }
}