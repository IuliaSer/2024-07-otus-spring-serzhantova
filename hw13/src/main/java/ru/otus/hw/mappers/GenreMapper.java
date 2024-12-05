package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Genre;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    public GenreDto convertToGenreDto(Genre genre) {
        return GenreDto
                .builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

    public Genre convertToEntity(GenreDto genre) {
        return Genre
                .builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
