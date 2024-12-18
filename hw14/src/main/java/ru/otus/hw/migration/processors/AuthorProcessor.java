package ru.otus.hw.migration.processors;


import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.entity.Author;

public class AuthorProcessor implements ItemProcessor<Author, AuthorDto> {

    @Override
    public AuthorDto process(Author item) {
        return new AuthorDto(item.getId(), item.getFullName());
    }
}