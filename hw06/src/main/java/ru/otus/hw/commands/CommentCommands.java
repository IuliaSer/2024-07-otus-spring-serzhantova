package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find all comments", key = "ac")
    public String findAllCommentsByBookId(long bookId) {
        return commentService.findAllByBookId(bookId).stream()
                .map(commentConverter::commentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentDtoToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    // сins newComment 1 1
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String message, long bookId) {
        var savedComment = commentService.insert(message, bookId);
        return commentConverter.commentDtoToString(savedComment);
    }

    // сupd 4 editedComment 3 2
    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String message, long bookId) {
        var savedComment = commentService.update(id, message, bookId);
        return commentConverter.commentDtoToString(savedComment);
    }

    // сdel 4
    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}