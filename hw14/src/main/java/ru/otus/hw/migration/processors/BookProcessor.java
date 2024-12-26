package ru.otus.hw.migration.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.entity.Book;

public class BookProcessor implements ItemProcessor<Book, ShortBookDto> {

    @Override
    public ShortBookDto process(Book item) {
        return new ShortBookDto(item.getId(), item.getTitle(), item.getAuthor().getId(), item.getGenre().getId());
    }
}