package ru.otus.hw.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFoundException(EntityNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error404");
        modelAndView.addObject("errors", e.getMessage());
        return modelAndView;
    }
}
