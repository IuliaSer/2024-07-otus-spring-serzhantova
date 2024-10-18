package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(commentMapper::convertToCommentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByBookId(String bookId) {
        return commentRepository
                .findAllByBookId(bookId)
                .stream()
                .map(commentMapper::convertToCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto insert(String message, String bookId) {
        return save("0", message, bookId);
    }

    @Override
    @Transactional
    public CommentDto update(String id, String message, String bookId) {
        return save(id, message, bookId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(String id, String message, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new Comment(id, message, book);
        return commentMapper.convertToCommentDto(commentRepository.save(comment));
    }
}