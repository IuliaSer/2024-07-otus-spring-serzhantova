package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.entity.Book;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final AclServiceWrapperService aclServiceWrapperService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить книгу по id: %s", id)));
        return bookMapper.convertToBookDto(book);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository
                .findAll()
                .stream()
                .map(bookMapper::convertToBookDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto insert(String title, long authorId, long genreId) {
        BookDto bookDto = save(0, title, authorId, genreId);
        aclServiceWrapperService.createPermission(bookMapper.convertToShortBookDto(bookDto), BasePermission.WRITE);
        return bookDto;
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.dto.ShortBookDto', 'WRITE')")
    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, long genreId) {
        return save(id, title, authorId, genreId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(long id, String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookMapper.convertToBookDto(bookRepository.save(book));
    }
}