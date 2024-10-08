package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        printQuestions();
    }

    private void printQuestions() {
        for (Question question: questionDao.findAll()) {
            ioService.printLine(question.text());
            ioService.printLine("");
            for (Answer answer: question.answers()) {
                ioService.printLine(answer.text());
            }
            ioService.printLine("");
        }
    }
}
