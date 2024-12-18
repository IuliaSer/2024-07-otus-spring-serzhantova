package ru.otus.hw.migration.processors;


import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Genre;

public class GenreProcessor implements ItemProcessor<Genre, GenreDto> {

    @Override
    public GenreDto process(Genre item) {
        return new GenreDto(item.getId(), item.getName());
    }
}