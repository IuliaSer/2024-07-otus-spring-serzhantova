package ru.otus.hw.exceptions;

public class WrongInputException extends RuntimeException {

    public WrongInputException(String message) {
        super(message);
    }
}
