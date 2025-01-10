package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.entity.Author;

@Component
public class AuthorMapper {
    
    public AuthorDto convertToAuthorDto(Author author) {
        return AuthorDto
                .builder()
                .id(author.getId())
                .fullName(author.getFullName())
                .build();
    }
}
