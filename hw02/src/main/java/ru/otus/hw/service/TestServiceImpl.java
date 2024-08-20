package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.WrongInputException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = false;

            askQuestion(question);
            String answerFromStudent = ioService.readString();
            isAnswerValid = isAnswerCorrect(question, answerFromStudent);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void askQuestion(Question question) {
        ioService.printFormattedLine("%s%n", question.text());

        for (Answer answer: question.answers()) {
            ioService.printLine(answer.text());
        }
    }

    private boolean isAnswerCorrect(Question question, String answerFromStudent) {
        List<Answer> answers = question.answers();
        if (answers.contains(new Answer(answerFromStudent, true))) {
            return true;
        } else if (answers.contains(new Answer(answerFromStudent, false))) {
            return false;
        } else {
            throw new WrongInputException("Your answer is not among the following");
        }
    }
}
