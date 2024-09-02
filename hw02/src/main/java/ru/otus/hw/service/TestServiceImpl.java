package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

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
        String prompt = "Choose the number of the answer";
        String errorMessage = "You enter wrong number. Try again";

        for (var question: questions) {
            askQuestion(question);
            int amountOfAnswers = question.answers().size();
            int studentAnswer = ioService.readIntForRangeWithPrompt(1, amountOfAnswers, prompt, errorMessage);
            Answer answer = getCorrespondingAnswer(question, studentAnswer);
            testResult.applyAnswer(question, answer.isCorrect());
        }
        return testResult;
    }

    private void askQuestion(Question question) {
        ioService.printFormattedLine("%s%n", question.text());

        int i = 1;
        for (Answer answer: question.answers()) {
            ioService.printFormattedLine("%d. %s", i++, answer.text());
        }
    }

    private Answer getCorrespondingAnswer(Question question, int studentAnswer) {
        return question.answers().get(studentAnswer - 1);
    }

}

